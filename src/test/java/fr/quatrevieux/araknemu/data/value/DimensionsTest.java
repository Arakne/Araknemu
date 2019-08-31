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

class DimensionsTest {
    @Test
    void data() {
        Dimensions dimensions = new Dimensions(15, 17);

        assertEquals(17, dimensions.height());
        assertEquals(15, dimensions.width());
    }

    @Test
    void equals() {
        assertNotEquals(null, new Dimensions(12, 15));
        assertNotEquals(new Dimensions(15, 12), new Dimensions(12, 15));
        assertEquals(new Dimensions(12, 15), new Dimensions(12, 15));
    }
}