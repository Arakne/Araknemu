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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.AbstractFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FighterListTest extends FightBaseCase {
    private Fight fight;
    private FighterList fighterList;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighterList = fight.fighters();
    }

    @Test
    void join() throws SQLException {
        Fighter fighter = new PlayerFighter(makeSimpleGamePlayer(10));
        fighterList.join(fighter, fight.map().get(146));

        assertSame(fight.map().get(146), fighter.cell());
        assertSame(fighter, fight.map().get(146).fighter());
        assertContains(fighter, fighterList.all());

        boolean found = false;

        for(Fighter f : fighterList){
            if(f == fighter){
                found = true;
                break;
            }
        }

        assertTrue(found);
    }

    @Test
    void joinOnStartedFightShouldNotAddToTurnList() throws SQLException {
        fight.nextState();

        Fighter fighter = new PlayerFighter(makeSimpleGamePlayer(10));
        fighterList.join(fighter, fight.map().get(146));

        assertSame(fight.map().get(146), fighter.cell());
        assertSame(fighter, fight.map().get(146).fighter());
        assertContains(fighter, fighterList.all());
        assertFalse(fight.turnList().fighters().contains(fighter));

        boolean found = false;

        for(Fighter f : fighterList){
            if(f == fighter){
                found = true;
                break;
            }
        }

        assertTrue(found);
    }

    @Test
    void joinTurnList() throws SQLException {
        fight.nextState();

        PlayableFighter fighter = new PlayerFighter(makeSimpleGamePlayer(10));
        fighterList.joinTurnList(fighter, fight.map().get(146));

        assertSame(fight.map().get(146), fighter.cell());
        assertSame(fighter, fight.map().get(146).fighter());
        assertContains(fighter, fighterList.all());
        assertTrue(fight.turnList().fighters().contains(fighter));

        boolean found = false;

        for(Fighter f : fighterList){
            if(f == fighter){
                found = true;
                break;
            }
        }

        assertTrue(found);
    }

    @Test
    void leaveNotStarted() throws SQLException {
        AtomicReference<FighterRemoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterRemoved.class, ref::set);

        Fighter fighter = new PlayerFighter(makeSimpleGamePlayer(10));
        fighterList.join(fighter, fight.map().get(146));
        assertContains(fighter, fighterList.all());

        fighterList.leave(fighter);

        assertFalse(fighterList.all().contains(fighter));
        assertSame(fighter, ref.get().fighter());
        assertSame(fight, ref.get().fight());

        boolean found = false;

        for(Fighter f : fighterList){
            if(f == fighter){
                found = true;
                break;
            }
        }

        assertFalse(found);
    }

    @Test
    void leaveStarted() throws SQLException {
        fight.nextState();

        AtomicReference<FighterRemoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterRemoved.class, ref::set);

        PlayableFighter fighter = new PlayerFighter(makeSimpleGamePlayer(10));
        fighterList.joinTurnList(fighter, fight.map().get(146));
        assertContains(fighter, fighterList.all());

        fighterList.leave(fighter);

        assertFalse(fighterList.all().contains(fighter));
        assertFalse(fight.turnList().fighters().contains(fighter));
        assertSame(fighter, ref.get().fighter());
        assertSame(fight, ref.get().fight());
    }

    @Test
    void alive() throws SQLException {
        fight.nextState();

        Fighter fighter = new PlayerFighter(makeSimpleGamePlayer(10));
        fighterList.join(fighter, fight.map().get(146));
        fighter.init();

        assertContains(fighter, fighterList.alive().collect(Collectors.toList()));
        assertContains(fighter, fighterList.all());

        fighter.life().kill(fighter);

        assertFalse(fighterList.alive().collect(Collectors.toList()).contains(fighter));
        assertContains(fighter, fighterList.all());
    }

    @Test
    void dispatch() throws NoSuchFieldException, IllegalAccessException {
        class Foo {}
        AtomicInteger ai = new AtomicInteger();

        player.fighter().dispatcher().add(Foo.class, foo -> ai.incrementAndGet());
        other.fighter().dispatcher().add(Foo.class, foo -> ai.incrementAndGet());

        fight.dispatchToAll(new Foo());
        assertEquals(2, ai.get());

        Field field = AbstractFighter.class.getDeclaredField("fight");
        field.setAccessible(true);
        field.set(other.fighter(), null); // Remove from fight

        ai.set(0);
        fight.dispatchToAll(new Foo());
        assertEquals(1, ai.get());
    }

    @Test
    void removeAll() throws SQLException {
        Fighter fighter = new PlayerFighter(makeSimpleGamePlayer(10));
        fighterList.join(fighter, fight.map().get(146));

        fighterList.removeAll(Collections.singleton(fighter));

        assertFalse(fighterList.all().contains(fighter));
        assertFalse(fight.map().get(146).hasFighter());
    }
}
