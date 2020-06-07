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

package fr.quatrevieux.araknemu.realm;

import fr.arakne.utils.encoding.PasswordEncoder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionKeyTest {
    @Test
    void constructor() {
        assertEquals("azerty", new ConnectionKey("azerty").key());
    }

    @Test
    void generate() {
        ConnectionKey key = new ConnectionKey();

        assertSame(key.key(), key.key());
        assertEquals(32, key.key().length());

        assertNotEquals(key.key(), new ConnectionKey().key());
    }

    @Test
    void decodeRandomKey() {
        for (int i = 0; i < 100; ++i) {
            String data = "my_secret_data";

            ConnectionKey key = new ConnectionKey();
            String encoded = cryptPassword(data, key.key());

            assertEquals(data, key.decode(encoded), "For key : " + key.key() + " encoded : " + encoded);
        }
    }

    @Test
    void decodeWithSpecialChars() {
        String data = "é#ç@à²";

        ConnectionKey key = new ConnectionKey("azertyuiop");
        String encoded = cryptPassword(data, key.key());

        assertEquals(data, key.decode(encoded));
    }

    @Test
    void decodeWithComplexKey() {
        String data = "my_secret_data";

        ConnectionKey key = new ConnectionKey("é#ç@à²{ùø*µ°~§a/_.");
        String encoded = cryptPassword(data, key.key());

        assertEquals(data, key.decode(encoded));
    }

    /**
     * Adapted from :
     * https://github.com/Emudofus/Dofus/blob/1.29/ank/utils/Crypt.as#L20
     */
    static public String cryptPassword(String pwd, String key) {
        return new PasswordEncoder(key).encode(pwd);
    }
}
