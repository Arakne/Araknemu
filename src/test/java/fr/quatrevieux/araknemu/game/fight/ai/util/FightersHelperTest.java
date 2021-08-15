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

import fr.quatrevieux.araknemu.game.fight.ai.AiBaseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void cells() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(123))
            .addEnemy(b -> b.cell(125))
            .addEnemy(b -> b.cell(135))
        );

        assertArrayEquals(new Object[] {ai.map().get(125), ai.map().get(135)}, helper().cells().toArray());
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

    private FightersHelper helper() {
        return ai.helper().enemies();
    }
}