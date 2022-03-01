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

package fr.quatrevieux.araknemu.data.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {
    @Test
    void data() {
        Position position = new Position(1254, 235);

        assertEquals(1254, position.map());
        assertEquals(235, position.cell());
        assertFalse(position.isNull());
    }

    @Test
    void equals() {
        Position position = new Position(1254, 235);

        assertNotEquals(null, position);
        assertNotEquals(position, null);
        assertNotEquals(new Position(74, 52), position);
        assertEquals(position, position);
        assertEquals(new Position(1254, 235), position);
    }

    @Test
    void newCell() {
        Position position = new Position(1254, 235);

        assertEquals(241, position.newCell(241).cell());
        assertNotSame(position, position.newCell(241).cell());
    }

    @Test
    void generateString() {
        assertEquals("(1254, 235)", new Position(1254, 235).toString());
    }
}