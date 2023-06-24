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

package fr.quatrevieux.araknemu.util;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class RandomStringUtilTest {
    @Test
    void neverSameValue() {
        RandomStringUtil generator = new RandomStringUtil(new Random(), "abcd");
        assertNotEquals(generator.generate(20), generator.generate(20));
    }

    @Test
    void generatedLength() {
        assertEquals(32, new RandomStringUtil(new Random(), "abcd").generate(32).length());
    }

    @Test
    void charset() {
        assertEquals("aaaa", new RandomStringUtil(new Random(), "a").generate(4));
    }
}
