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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParseUtilsTest {
    @Test
    void parsePositive() {
        assertEquals(5, ParseUtils.parsePositiveInt("5"));
        assertEquals(1, ParseUtils.parsePositiveInt("1"));
        assertEquals(123, ParseUtils.parsePositiveInt("123"));

        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parsePositiveInt("-1"));
        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parsePositiveInt("-5"));
        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parsePositiveInt("0"));
        assertThrows(NumberFormatException.class, () -> ParseUtils.parsePositiveInt("invalid"));
    }

    @Test
    void parseNonNegative() {
        assertEquals(5, ParseUtils.parseNonNegativeInt("5"));
        assertEquals(1, ParseUtils.parseNonNegativeInt("1"));
        assertEquals(0, ParseUtils.parseNonNegativeInt("0"));
        assertEquals(123, ParseUtils.parseNonNegativeInt("123"));

        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parseNonNegativeInt("-1"));
        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parseNonNegativeInt("-5"));
        assertThrows(NumberFormatException.class, () -> ParseUtils.parseNonNegativeInt("invalid"));
    }

    @Test
    void parseNonNegativeWithBase() {
        assertEquals(11, ParseUtils.parseNonNegativeInt("b", 16));
        assertEquals(1, ParseUtils.parseNonNegativeInt("1", 16));
        assertEquals(0, ParseUtils.parseNonNegativeInt("0", 16));
        assertEquals(496, ParseUtils.parseNonNegativeInt("1f0", 16));

        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parseNonNegativeInt("-1", 16));
        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parseNonNegativeInt("-5", 16));
        assertThrows(NumberFormatException.class, () -> ParseUtils.parseNonNegativeInt("invalid", 16));
    }

    @Test
    void parseNonNegativeOrNegativeOne() {
        assertEquals(5, ParseUtils.parseNonNegativeOrNegativeOneInt("5"));
        assertEquals(1, ParseUtils.parseNonNegativeOrNegativeOneInt("1"));
        assertEquals(0, ParseUtils.parseNonNegativeOrNegativeOneInt("0"));
        assertEquals(-1, ParseUtils.parseNonNegativeOrNegativeOneInt("-1"));
        assertEquals(123, ParseUtils.parseNonNegativeOrNegativeOneInt("123"));

        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parseNonNegativeOrNegativeOneInt("-5"));
        assertThrows(NumberFormatException.class, () -> ParseUtils.parseNonNegativeOrNegativeOneInt("invalid"));
    }

    @Test
    void parseDecimalChar() {
        assertEquals(0, ParseUtils.parseDecimalChar('0'));
        assertEquals(9, ParseUtils.parseDecimalChar('9'));

        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parseDecimalChar(' '));
        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parseDecimalChar('@'));
    }

    @Test
    void parseDecimalCharWithString() {
        assertEquals(0, ParseUtils.parseDecimalChar("0"));
        assertEquals(9, ParseUtils.parseDecimalChar("9"));
        assertEquals(1, ParseUtils.parseDecimalChar("123"));

        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parseDecimalChar(" "));
        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parseDecimalChar("@"));
        assertThrows(IllegalArgumentException.class, () -> ParseUtils.parseDecimalChar(""));
    }
}
