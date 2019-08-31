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

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.*;

class Base64Test {
    @Test
    void ordSuccess() {
        String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";

        for (int i = 0; i < charset.length(); ++i) {
            assertEquals(i, Base64.ord(charset.charAt(i)));
        }
    }

    @Test
    void ordInvalidChar() {
        assertThrows(InvalidParameterException.class, () -> Base64.ord('#'));
    }

    @Test
    void encodeSingleChar() {
        String charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";

        for (int i = 0; i < charset.length(); ++i) {
            assertEquals(Character.toString(charset.charAt(i)), Base64.encode(i, 1));
        }
    }

    @Test
    void encodeWithTwoChars() {
        assertEquals("cr", Base64.encode(145, 2));
    }

    @Test
    void encodeWithTooSmallNumberWillKeepLength() {
        assertEquals("aac", Base64.encode(2, 3));
    }

    @Test
    void chr() {
        assertEquals('a', Base64.chr(0));
        assertEquals('_', Base64.chr(63));
        assertEquals('c', Base64.chr(2));
    }

    @Test
    void decodeWithOneChar() {
        assertEquals(0, Base64.decode("a"));
        assertEquals(2, Base64.decode("c"));
        assertEquals(63, Base64.decode("_"));
    }

    @Test
    void decode() {
        assertEquals(458, Base64.decode("hk"));
    }

    @Test
    void decodeEncodeTwoChars() {
        assertEquals(741, Base64.decode(Base64.encode(741, 2)));
        assertEquals(951, Base64.decode(Base64.encode(951, 2)));
        assertEquals(325, Base64.decode(Base64.encode(325, 2)));
        assertEquals(769, Base64.decode(Base64.encode(769, 2)));
    }
}
