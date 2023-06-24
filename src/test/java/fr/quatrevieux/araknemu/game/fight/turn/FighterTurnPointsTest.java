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

package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.event.ActionPointsUsed;
import fr.quatrevieux.araknemu.game.fight.turn.event.MovementPointsUsed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class FighterTurnPointsTest extends FightBaseCase {
    private Fight fight;
    private Fighter fighter;
    private FighterTurnPoints points;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();

        points = new FighterTurnPoints(fight, fighter);
    }

    @Test
    void defaults() {
        assertEquals(3, points.movementPoints());
        assertEquals(6, points.actionPoints());
    }

    @Test
    void useMovementPoints() {
        AtomicReference<MovementPointsUsed> ref = new AtomicReference<>();
        fight.dispatcher().add(MovementPointsUsed.class, ref::set);

        points.useMovementPoints(2);

        assertSame(fighter, ref.get().fighter());
        assertEquals(2, ref.get().quantity());

        assertEquals(1, points.movementPoints());
        assertEquals(2, points.usedMovementPoints());
    }

    @Test
    void useActionPoints() {
        AtomicReference<ActionPointsUsed> ref = new AtomicReference<>();
        fight.dispatcher().add(ActionPointsUsed.class, ref::set);

        points.useActionPoints(2);

        assertSame(fighter, ref.get().fighter());
        assertEquals(2, ref.get().quantity());

        assertEquals(4, points.actionPoints());
        assertEquals(2, points.usedActionPoints());
    }

    @Test
    void addMovementPoints() {
        points.addMovementPoints(3);

        assertEquals(6, points.movementPoints());
    }

    @Test
    void removeMovementPoints() {
        points.removeMovementPoints(2);

        assertEquals(1, points.movementPoints());
    }

    @Test
    void removeAndAddMovementPoints() {
        points.removeMovementPoints(10);
        assertEquals(0, points.movementPoints());

        points.addMovementPoints(2);
        assertEquals(2, points.movementPoints());
    }

    @Test
    void addActionPoints() {
        points.addActionPoints(3);

        assertEquals(9, points.actionPoints());
    }

    @Test
    void removeActionPoints() {
        points.removeActionPoints(2);

        assertEquals(4, points.actionPoints());
    }

    @Test
    void removeAndAddActionPoints() {
        points.removeActionPoints(10);
        assertEquals(0, points.actionPoints());

        points.addActionPoints(2);
        assertEquals(2, points.actionPoints());
    }
}
