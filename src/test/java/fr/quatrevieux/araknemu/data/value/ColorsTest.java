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

class ColorsTest {
    @Test
    void toArray() {
        Colors colors = new Colors(123, 456, 789);

        assertArrayEquals(new int[] {123, 456, 789}, colors.toArray());
    }

    @Test
    void toHexArray() {
        Colors colors = new Colors(123, 456, 789);

        assertArrayEquals(new String[] {"7b", "1c8", "315"}, colors.toHexArray());
    }

    @Test
    void toHexArrayDefaultColors() {
        Colors colors = new Colors(-1, -1, 789);

        assertArrayEquals(new String[] {"-1", "-1", "315"}, colors.toHexArray());
    }
}
