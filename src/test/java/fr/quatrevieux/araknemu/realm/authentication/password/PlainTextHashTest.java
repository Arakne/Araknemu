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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.realm.authentication.password;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlainTextHashTest {
    private PlainTextHash hash;

    @BeforeEach
    void setUp() {
        hash = new PlainTextHash();
    }

    @Test
    void parse() {
        Password password = hash.parse("test");

        assertEquals("test", password.toString());
        assertTrue(password.check("test"));
        assertFalse(password.check("invalid"));
        assertSame(hash, password.algorithm());
        assertFalse(password.needRehash());
    }

    @Test
    void hash() {
        Password password = hash.hash("test");

        assertEquals("test", password.toString());
        assertTrue(password.check("test"));
        assertFalse(password.check("invalid"));
        assertSame(hash, password.algorithm());
        assertFalse(password.needRehash());
    }

    @Test
    void supports() {
        assertTrue(hash.supports("test"));
    }

    @Test
    void name() {
        assertEquals("plain", hash.name());
    }
}
