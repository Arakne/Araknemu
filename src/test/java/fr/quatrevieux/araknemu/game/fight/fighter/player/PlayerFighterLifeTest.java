/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterLifeChanged;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class PlayerFighterLifeTest extends FightBaseCase {
    private PlayerFighterLife life;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();

        life = new PlayerFighterLife(gamePlayer(true).properties().life(), fighter);
    }

    @Test
    void initAlreadyInitialised() {
        life.init();

        assertThrows(IllegalStateException.class, () -> life.init());
    }

    @Test
    void defaults() throws SQLException, ContainerException {
        assertEquals(gamePlayer().properties().life().current(), life.current());
        assertEquals(gamePlayer().properties().life().max(), life.max());
        assertFalse(life.dead());
    }

    @Test
    void notInit() throws SQLException, ContainerException {
        gamePlayer().properties().life().set(100);

        assertEquals(100, life.current());
        assertEquals(gamePlayer().properties().life().max(), life.max());
        assertThrows(IllegalStateException.class, () -> life.alter(fighter, -10));
        assertThrows(IllegalStateException.class, () -> life.kill(fighter));
        assertThrows(IllegalStateException.class, () -> life.alterMax(fighter, 10));
    }

    @Test
    void initialized() throws SQLException, ContainerException {
        int current = life.current(),
            max = life.max()
        ;

        life.init();

        gamePlayer().properties().life().set(5);

        assertEquals(current, life.current());
        assertEquals(max, life.max());
    }

    @Test
    void alterOnDamage() {
        player.properties().life().set(100);
        life.init();

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(-10, life.alter(caster, -10));
        assertEquals(90, life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(-10, ref.get().value());
    }

    @Test
    void alterOnDamageHigherThanCurrentLife() {
        player.properties().life().set(100);
        life.init();

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(-100, life.alter(caster, -150));
        assertEquals(0, life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(-100, ref.get().value());
    }

    @Test
    void alterOnHeal() {
        player.properties().life().set(100);
        life.init();

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(10, life.alter(caster, 10));
        assertEquals(110, life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(10, ref.get().value());
    }

    @Test
    void alterOnHealHigherThanMax() {
        player.properties().life().set(100);
        life.init();

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(195, life.alter(caster, 1000));
        assertEquals(life.max(), life.current());

        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertEquals(195, ref.get().value());
    }

    /**
     * #56 : Dot not heal when dead
     */
    @Test
    void alterHealIfDead() {
        life.init();
        life.alter(fighter, -1000);

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(0, life.alter(caster, 1000));
        assertEquals(0, life.current());
        assertTrue(life.dead());
        assertNull(ref.get());
    }

    /**
     * #56 : Dot not heal when dead
     */
    @Test
    void alterDamageIfDead() {
        life.init();
        life.alter(fighter, -1000);

        AtomicReference<FighterLifeChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterLifeChanged.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        assertEquals(0, life.alter(caster, -1000));
        assertEquals(0, life.current());
        assertTrue(life.dead());
        assertNull(ref.get());
    }

    @Test
    void alterOnDie() {
        player.properties().life().set(100);
        life.init();

        AtomicReference<FighterDie> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterDie.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        life.alter(caster, -1000);

        assertEquals(0, life.current());
        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertTrue(life.dead());
    }

    @Test
    void alterMax() {
        player.properties().life().set(100);
        life.init();

        life.alterMax(Mockito.mock(Fighter.class), 100);

        assertEquals(395, life.max());
        assertEquals(200, life.current());
    }

    @Test
    void kill() {
        life.init();

        AtomicReference<FighterDie> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterDie.class, ref::set);

        Fighter caster = Mockito.mock(Fighter.class);

        life.kill(caster);

        assertEquals(0, life.current());
        assertSame(caster, ref.get().caster());
        assertSame(fighter, ref.get().fighter());
        assertTrue(life.dead());
    }
}
