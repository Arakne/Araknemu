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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BitSetTest {
    enum MyItems {
        A, B, C
    }

    private BitSet<MyItems> set;

    @BeforeEach
    void setUp() {
        set = new BitSet<>();
    }

    @Test
    void defaults() {
        assertFalse(set.check(MyItems.A));
        assertFalse(set.check(MyItems.B));
        assertFalse(set.check(MyItems.C));

        assertEquals(0, set.toInt());
    }

    @Test
    void setUnsetOne() {
        assertTrue(set.set(MyItems.A));
        assertTrue(set.check(MyItems.A));

        assertTrue(set.unset(MyItems.A));
        assertFalse(set.check(MyItems.A));
    }

    @Test
    void setAlreadySet() {
        assertTrue(set.set(MyItems.A));
        assertTrue(set.check(MyItems.A));

        assertFalse(set.set(MyItems.A));
        assertTrue(set.check(MyItems.A));
    }

    @Test
    void multiple() {
        assertTrue(set.set(MyItems.A));
        assertTrue(set.set(MyItems.C));

        assertTrue(set.check(MyItems.A));
        assertTrue(set.check(MyItems.C));
        assertFalse(set.check(MyItems.B));

        assertFalse(set.unset(MyItems.B));
        assertTrue(set.check(MyItems.A));
        assertTrue(set.check(MyItems.C));
        assertFalse(set.check(MyItems.B));

        assertTrue(set.unset(MyItems.A));
        assertFalse(set.check(MyItems.A));
        assertTrue(set.check(MyItems.C));
        assertFalse(set.check(MyItems.B));
    }

    @Test
    void toInt() {
        assertEquals(0, set.toInt());

        set.set(MyItems.A);
        assertEquals(1, set.toInt());

        set.set(MyItems.B);
        assertEquals(3, set.toInt());

        set.unset(MyItems.A);
        assertEquals(2, set.toInt());

        set.set(MyItems.C);
        assertEquals(6, set.toInt());
    }
}
