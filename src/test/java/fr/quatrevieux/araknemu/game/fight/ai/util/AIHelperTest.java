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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.util;

import fr.arakne.utils.maps.MapCell;
import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AIHelperTest extends AiBaseCase {
    @Test
    void canMoveOk() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
        );

        assertTrue(ai.helper().canMove());
    }

    @Test
    void canMoveNoMP() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
        );

        removeAllMP();

        assertFalse(ai.helper().canMove());
    }

    @Test
    void blockedByFighters() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(210))
            .addEnemy(b -> b.cell(195))
            .addEnemy(b -> b.cell(196))
        );

        assertFalse(ai.helper().canMove());
    }

    @Test
    void canCastOk() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
        );

        assertTrue(ai.helper().canCast());
    }

    @Test
    void canCastNoAP() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
        );

        removeAllAP();

        assertFalse(ai.helper().canCast());
    }

    @Test
    void canCastNoAvailableSpells() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
        );

        setAP(1);

        assertFalse(ai.helper().canCast());
    }

    @Test
    void points() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
        );

        assertEquals(6, ai.helper().actionPoints());
        assertEquals(3, ai.helper().movementPoints());
        assertTrue(ai.helper().hasActionPoints());
        assertTrue(ai.helper().hasMovementPoints());

        setAP(3);
        setMP(1);

        assertEquals(3, ai.helper().actionPoints());
        assertEquals(1, ai.helper().movementPoints());
        assertTrue(ai.helper().hasActionPoints());
        assertTrue(ai.helper().hasMovementPoints());

        removeAllAP();
        removeAllMP();

        assertEquals(0, ai.helper().actionPoints());
        assertEquals(0, ai.helper().movementPoints());
        assertFalse(ai.helper().hasActionPoints());
        assertFalse(ai.helper().hasMovementPoints());
    }

    @Test
    void withPosition() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
        );

        AIHelper helper = ai.helper().withPosition(ai.map().get(111));

        assertNotSame(helper, ai.helper());
        CastSimulation simulation = helper.spells()
            .caster(fight.actions().cast().validator())
            .simulate(container.get(Simulator.class))
            .findAny()
            .get();

        assertEquals(111, simulation.caster().cell().id());
        assertEquals(simulation.caster(), simulation.caster().cell().fighter());
        assertFalse(simulation.caster().cell().map().get(123).hasFighter());

        assertEquals(123, fighter.cell().id());
        assertFalse(fight.map().get(111).hasFighter());
        assertTrue(fight.map().get(123).hasFighter());
    }

    @Test
    void helpers() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
        );

        assertNotNull(ai.helper().cells());
        assertNotNull(ai.helper().spells());
        assertNotNull(ai.helper().enemies());
        assertNotNull(ai.helper().allies());

        assertSame(ai.helper().cells(), ai.helper().cells());
        assertSame(ai.helper().spells(), ai.helper().spells());
        assertSame(ai.helper().enemies(), ai.helper().enemies());
        assertSame(ai.helper().allies(), ai.helper().allies());
    }

    @Test
    void alliesShouldIgnoreSelf() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addAlly(b -> b.cell(135))
            .addAlly(b -> b.cell(150))
            .addEnemy(b -> b.cell(125))
        );

        assertEquals(2, ai.helper().allies().count());
        assertArrayEquals(new int [] {135, 150}, ai.helper().allies().cells().mapToInt(MapCell::id).sorted().toArray());
    }

    @Test
    void enemies() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addAlly(b -> b.cell(150))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(132))
        );

        assertEquals(2, ai.helper().enemies().count());
        assertArrayEquals(new int [] {125, 132}, ai.helper().enemies().cells().mapToInt(MapCell::id).toArray());
    }
}
