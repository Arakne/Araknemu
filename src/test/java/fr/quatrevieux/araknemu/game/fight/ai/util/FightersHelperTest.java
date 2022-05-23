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

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FightersHelperTest extends AiBaseCase {
    @Test
    void stream() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(135))
        );

        assertEquals(2, helper().stream().count());
        assertArrayEquals(new Object[] {getEnemy(0), getEnemy(1)}, helper().stream().toArray());
    }

    @Test
    void adjacent() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(210))
            .addEnemy(b -> b.cell(195))
            .addEnemy(b -> b.cell(196))
            .addEnemy(b -> b.cell(123))
        );

        assertArrayEquals(new Object[] {getEnemy(0), getEnemy(1)}, helper().adjacent().toArray());
        assertArrayEquals(new Object[] {getEnemy(2)}, helper().adjacent(ai.map().get(138)).toArray());
        assertArrayEquals(new Object[] {}, helper().adjacent(ai.map().get(120)).toArray());
    }

    @Test
    void adjacentShouldFilterHiddenFighters() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(210))
            .addEnemy(b -> b.cell(195))
            .addEnemy(b -> b.cell(196))
            .addEnemy(b -> b.cell(123))
        );

        getEnemy(1).setHidden(getEnemy(1), true);

        assertArrayEquals(new Object[] {getEnemy(0)}, helper().adjacent().toArray());
        assertArrayEquals(new Object[] {getEnemy(2)}, helper().adjacent(ai.map().get(138)).toArray());
        assertArrayEquals(new Object[] {}, helper().adjacent(ai.map().get(120)).toArray());
    }

    @Test
    void cells() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(135))
        );

        assertArrayEquals(new Object[] {ai.map().get(125), ai.map().get(135)}, helper().cells().toArray());
    }

    @Test
    void cellsShouldFilterHiddenFighter() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(135))
        );

        getEnemy(0).setHidden(getEnemy(0), true);

        assertArrayEquals(new Object[] {ai.map().get(135)}, helper().cells().toArray());
    }

    @Test
    void count() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(135))
        );

        assertEquals(2, helper().count());
    }

    @Test
    void countShouldNotIgnoreHiddenFighter() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(135))
        );

        getEnemy(0).setHidden(getEnemy(0), true);

        assertEquals(2, helper().count());
    }

    @Test
    void nearest() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(135))
        );

        assertEquals(getEnemy(0), helper().nearest().get());

        configureFight(fb -> fb
            .addSelf(b -> b.cell(210))
            .addEnemy(b -> b.cell(195).currentLife(50))
            .addEnemy(b -> b.cell(196).currentLife(15))
            .addEnemy(b -> b.cell(123))
        );

        assertEquals(getEnemy(1), helper().nearest().get());
    }

    @Test
    void nearestWithHiddenFighterShouldBeIgnored() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(135))
        );

        getEnemy(0).setHidden(getEnemy(0), true);
        assertEquals(getEnemy(1), helper().nearest().get());

        getEnemy(1).setHidden(getEnemy(0), true);
        assertFalse(helper().nearest().isPresent());
    }

    @Test
    void inRange() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(135))
        );

        assertArrayEquals(new Object[] {}, helper().inRange(new Interval(0, 1)).toArray());
        assertArrayEquals(new Object[] {getEnemy(0)}, helper().inRange(new Interval(0, 4)).toArray());
        assertArrayEquals(new Object[] {getEnemy(0), getEnemy(1)}, helper().inRange(new Interval(0, 5)).toArray());
        assertArrayEquals(new Object[] {getEnemy(1)}, helper().inRange(new Interval(5, 5)).toArray());
    }

    @Test
    void inRangeWithHiddenFighterShouldBeIgnored() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(135))
        );

        getEnemy(0).setHidden(getEnemy(0), true);

        assertArrayEquals(new Object[] {}, helper().inRange(new Interval(0, 4)).toArray());
        assertArrayEquals(new Object[] {getEnemy(1)}, helper().inRange(new Interval(0, 5)).toArray());
    }

    private FightersHelper helper() {
        return ai.helper().enemies();
    }
}