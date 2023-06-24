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

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterStateChanged;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FighterStatesTest extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;
    private States states;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        states = new FighterStates(fighter);
        fight.state(PlacementState.class).startFight();
    }

    @Test
    void pushNewState() {
        AtomicReference<FighterStateChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterStateChanged.class, ref::set);

        states.push(5);

        assertTrue(states.has(5));
        requestStack.assertLast(ActionEffect.addState(fighter, 5));
        assertSame(fighter, ref.get().fighter());
        assertEquals(5, ref.get().state());
        assertEquals(FighterStateChanged.Type.ADD, ref.get().type());
    }

    @Test
    void pushStateAlreadyActive() {
        states.push(5);
        requestStack.clear();

        AtomicReference<FighterStateChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterStateChanged.class, ref::set);

        states.push(5);
        requestStack.assertEmpty();
        assertNull(ref.get());
    }

    @Test
    void pushStateAlreadyActiveWithHigherDuration() {
        states.push(5, 3);
        requestStack.clear();

        AtomicReference<FighterStateChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterStateChanged.class, ref::set);

        states.push(5, 10);
        assertSame(fighter, ref.get().fighter());
        assertEquals(5, ref.get().state());
        assertEquals(FighterStateChanged.Type.UPDATE, ref.get().type());
    }

    @Test
    void pushStateAlreadyActiveWithLowerDuration() {
        states.push(5, 3);

        AtomicReference<FighterStateChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterStateChanged.class, ref::set);

        states.push(5, 2);
        assertNull(ref.get());
    }

    @Test
    void pushAlreadyActiveWithIndefiniteDuration() {
        states.push(5, 3);

        AtomicReference<FighterStateChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterStateChanged.class, ref::set);

        states.push(5);
        assertSame(fighter, ref.get().fighter());
        assertEquals(5, ref.get().state());
        assertEquals(FighterStateChanged.Type.UPDATE, ref.get().type());
    }

    @Test
    void hasAll() {
        states.push(5);
        states.push(4);
        states.push(3);

        assertTrue(states.hasAll(new int[] {3, 4, 5}));
        assertTrue(states.hasAll(new int[] {3, 5}));
        assertFalse(states.hasAll(new int[] {3, 5, 8}));
    }

    @Test
    void hasOne() {
        states.push(5);
        states.push(4);
        states.push(3);

        assertTrue(states.hasOne(new int[] {3, 5}));
        assertTrue(states.hasOne(new int[] {5, 8}));
        assertFalse(states.hasOne(new int[] {8}));
    }

    @Test
    void refresh() {
        AtomicReference<FighterStateChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterStateChanged.class, ref::set);

        states.push(3);
        states.push(2, 1);
        states.push(1, 2);

        states.refresh();

        assertTrue(states.has(3));
        assertTrue(states.has(1));
        assertFalse(states.has(2));

        assertEquals(2, ref.get().state());
        assertEquals(FighterStateChanged.Type.REMOVE, ref.get().type());

        states.refresh();

        assertTrue(states.has(3));
        assertFalse(states.has(2));
        assertFalse(states.has(1));

        assertEquals(1, ref.get().state());
        assertEquals(FighterStateChanged.Type.REMOVE, ref.get().type());
    }

    @Test
    void removeNotFound() {
        AtomicReference<FighterStateChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterStateChanged.class, ref::set);

        states.remove(5);

        assertNull(ref.get());
    }

    @Test
    void remove() {
        states.push(5);

        AtomicReference<FighterStateChanged> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterStateChanged.class, ref::set);

        states.remove(5);

        assertFalse(states.has(5));
        assertEquals(5, ref.get().state());
        assertEquals(FighterStateChanged.Type.REMOVE, ref.get().type());
    }
}
