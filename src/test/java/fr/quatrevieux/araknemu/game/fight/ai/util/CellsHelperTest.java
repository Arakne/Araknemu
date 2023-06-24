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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CellsHelperTest extends AiBaseCase {
    @Test
    void stream() {
        assertTrue(helper().stream().anyMatch(c -> c.id() == 123));
        assertTrue(helper().stream().anyMatch(c -> c.id() == 125));
        assertTrue(helper().stream().anyMatch(c -> c.id() == 150));
        assertFalse(helper().stream().anyMatch(c -> c.id() == 145));
    }

    @Test
    void adjacent() {
        assertArrayEquals(new int[] {138, 137, 108, 109}, helper().adjacent().mapToInt(MapCell::id).toArray());
        assertArrayEquals(new int[] {195, 196}, helper().adjacent(ai.map().get(210)).mapToInt(MapCell::id).toArray());
    }

    @Test
    void pathfinder() {
        assertNotNull(helper().pathfinder());
        assertNotSame(helper().pathfinder(), helper().pathfinder());
    }

    private CellsHelper helper() {
        if (fight == null) {
            configureFight(fb -> fb
                .addSelf(b -> b.cell(123))
                .addEnemy(b -> b.cell(125))
            );
        }

        return ai.helper().cells();
    }
}
