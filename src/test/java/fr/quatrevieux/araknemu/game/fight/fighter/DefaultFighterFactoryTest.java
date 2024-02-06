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

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.PlayerFighterCreated;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.listener.fight.SendFightJoined;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.LeaveOnDisconnect;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendFightLeaved;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendSpellBoosted;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.SendStats;
import fr.quatrevieux.araknemu.game.listener.fight.fighter.StopFightSession;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultFighterFactoryTest extends FightBaseCase {
    private DefaultFighterFactory factory;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new DefaultFighterFactory(
            dispatcher = new DefaultListenerAggregate()
        );
    }

    @Test
    void createPlayerFighter() throws SQLException, ContainerException {
        AtomicReference<PlayerFighterCreated> ref = new AtomicReference<>();
        dispatcher.add(PlayerFighterCreated.class, ref::set);

        GamePlayer player = gamePlayer();

        PlayerFighter fighter = factory.create(player);

        assertSame(fighter, ref.get().fighter());
        assertSame(player, fighter.player());

        assertTrue(fighter.dispatcher().has(SendFightJoined.class));
        assertTrue(fighter.dispatcher().has(StopFightSession.OnLeave.class));
        assertTrue(fighter.dispatcher().has(StopFightSession.OnFinish.class));
        assertTrue(fighter.dispatcher().has(SendFightLeaved.class));
        assertTrue(fighter.dispatcher().has(LeaveOnDisconnect.class));
        assertTrue(fighter.dispatcher().has(SendStats.class));
        assertTrue(fighter.dispatcher().has(SendSpellBoosted.class));
    }

    @Test
    void generate() {
        Fighter fighter = Mockito.mock(Fighter.class);

        assertSame(fighter, factory.generate(id -> {
            assertEquals(-1, id);
            return fighter;
        }));
        assertSame(fighter, factory.generate(id -> {
            assertEquals(-2, id);
            return fighter;
        }));
    }

    @Test
    void createMonsterFighter() throws Exception {
        dataSet.pushMonsterTemplates();
        dataSet.pushMonsterSpells();

        Fight fight = createFight();
        Monster monster = container.get(MonsterService.class).load(36).get(1);

        Fighter fighter = factory.create(monster, fight.team(1));

        assertInstanceOf(MonsterFighter.class, fighter);
        assertEquals(-1, fighter.id());
        assertSame(fight.team(1), fighter.team());
        assertSame(monster, ((MonsterFighter) fighter).monster());

        assertEquals(-2, factory.create(monster, fight.team(1)).id());
        assertEquals(-3, factory.create(monster, fight.team(1)).id());
    }

    @Test
    void generatedIdOverflow() throws Exception {
        dataSet.pushMonsterTemplates();
        dataSet.pushMonsterSpells();

        Fight fight = createFight();
        Monster monster = container.get(MonsterService.class).load(36).get(1);

        factory = new DefaultFighterFactory(new DefaultListenerAggregate(), -3);

        assertEquals(-1, factory.create(monster, fight.team(1)).id());
        assertEquals(-2, factory.create(monster, fight.team(1)).id());
        assertEquals(-3, factory.create(monster, fight.team(1)).id());
        assertEquals(-1, factory.create(monster, fight.team(1)).id());
        assertEquals(-2, factory.create(monster, fight.team(1)).id());
        assertEquals(-3, factory.create(monster, fight.team(1)).id());
    }
}
