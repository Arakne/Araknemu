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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsserterTest {
    @Test
    void assertPositive() {
        assertEquals(1, Asserter.assertPositive(1));
        assertEquals(123, Asserter.assertPositive(123));

        assertThrows(IllegalArgumentException.class, () -> Asserter.assertPositive(0));
        assertThrows(IllegalArgumentException.class, () -> Asserter.assertPositive(-1));
        assertThrows(IllegalArgumentException.class, () -> Asserter.assertPositive(-12));
    }

    @Test
    void assertNonNegative() {
        assertEquals(0, Asserter.assertNonNegative(0));
        assertEquals(1, Asserter.assertNonNegative(1));
        assertEquals(123, Asserter.assertNonNegative(123));

        assertThrows(IllegalArgumentException.class, () -> Asserter.assertNonNegative(-1));
        assertThrows(IllegalArgumentException.class, () -> Asserter.assertNonNegative(-12));
    }

    @Test
    void assertNonNegativeWithLong() {
        assertEquals(0L, Asserter.assertNonNegative(0L));
        assertEquals(1L, Asserter.assertNonNegative(1L));
        assertEquals(123L, Asserter.assertNonNegative(123L));

        assertThrows(IllegalArgumentException.class, () -> Asserter.assertNonNegative(-1L));
        assertThrows(IllegalArgumentException.class, () -> Asserter.assertNonNegative(-12L));
    }

    @Test
    void assertGTENegativeOne() {
        assertEquals(-1, Asserter.assertGTENegativeOne(-1));
        assertEquals(0, Asserter.assertGTENegativeOne(0));
        assertEquals(1, Asserter.assertGTENegativeOne(1));
        assertEquals(123, Asserter.assertGTENegativeOne(123));

        assertThrows(IllegalArgumentException.class, () -> Asserter.assertGTENegativeOne(-12));
    }

    @Test
    void assertPercent() {
        assertEquals(0, Asserter.assertPercent(0));
        assertEquals(1, Asserter.assertPercent(1));
        assertEquals(15, Asserter.assertPercent(15));
        assertEquals(100, Asserter.assertPercent(100));

        assertThrows(IllegalArgumentException.class, () -> Asserter.assertPercent(-12));
        assertThrows(IllegalArgumentException.class, () -> Asserter.assertPercent(-1));
        assertThrows(IllegalArgumentException.class, () -> Asserter.assertPercent(101));
        assertThrows(IllegalArgumentException.class, () -> Asserter.assertPercent(125));
    }

    @Test
    void assertIndexFor() {
        Object[] arr = new Object[10];

        assertEquals(0, Asserter.assertIndexFor(arr, 0));
        assertEquals(5, Asserter.assertIndexFor(arr, 5));
        assertEquals(9, Asserter.assertIndexFor(arr, 9));

        assertThrows(IndexOutOfBoundsException.class, () -> Asserter.assertIndexFor(arr, -12));
        assertThrows(IndexOutOfBoundsException.class, () -> Asserter.assertIndexFor(arr, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> Asserter.assertIndexFor(arr, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> Asserter.assertIndexFor(arr, 100));
    }

    @Test
    void casts() {
        assertEquals(5, Asserter.castPositive(5));
        assertEquals(5L, Asserter.castPositive(5L));
        assertEquals(5, Asserter.castNonNegative(5));
        assertEquals(5L, Asserter.castNonNegative(5L));
    }
}
