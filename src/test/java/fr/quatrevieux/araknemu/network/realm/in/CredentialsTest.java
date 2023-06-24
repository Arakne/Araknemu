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

package fr.quatrevieux.araknemu.network.realm.in;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CredentialsTest {
    @Test
    public void success() {
        Packet packet = Credentials.parser().parse("authenticate\n#1my_hash");

        assertTrue(packet instanceof Credentials);

        Credentials credentials = (Credentials) packet;
        assertEquals("authenticate", credentials.username());
        assertEquals(Credentials.Method.VIGENERE_BASE_64, credentials.method());
        assertEquals("my_hash", credentials.password());
    }

    @Test
    public void noPassword() {
        assertThrows(ParsePacketException.class, () -> Credentials.parser().parse("invalid"), "Missing password");
    }

    @Test
    public void invalidHashFormat() {
        assertThrows(ParsePacketException.class, () -> Credentials.parser().parse("authenticate\ninvalid"), "Invalid hash format");
    }

    @Test
    public void invalidMethod() {
        assertThrows(ParsePacketException.class, () -> Credentials.parser().parse("authenticate\n#8hash"), "Invalid cypher method");
    }

    @Test
    public void methodGetInvalidChar() {
        assertThrows(NumberFormatException.class, () -> Credentials.Method.get('a'));
    }

    @Test
    public void methodGetInvalidMethod() {
        assertThrows(IndexOutOfBoundsException.class, () -> Credentials.Method.get('5'));
    }

    @Test
    public void methodGetValid() {
        assertEquals(Credentials.Method.NONE, Credentials.Method.get('0'));
        assertEquals(Credentials.Method.VIGENERE_BASE_64, Credentials.Method.get('1'));
        assertEquals(Credentials.Method.MD5, Credentials.Method.get('2'));
    }
}
