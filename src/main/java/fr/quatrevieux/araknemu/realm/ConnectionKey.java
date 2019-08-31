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

import fr.quatrevieux.araknemu.util.Base64;
import fr.quatrevieux.araknemu.util.RandomStringUtil;

import java.security.SecureRandom;

/**
 * Authentication token
 */
final public class ConnectionKey {
    /**
     * The connection key
     */
    final private String key;

    /**
     * The random generator for generate key
     */
    final static private RandomStringUtil rand = new RandomStringUtil(
        new SecureRandom(),
        "abcdefghijklmnopqrstuvwxyz"
    );

    public ConnectionKey(String key) {
        this.key = key;
    }

    /**
     * Generate a new key
     */
    public ConnectionKey() {
        this(rand.generate(32));
    }

    /**
     * Get the key value
     * @return
     */
    public String key() {
        return key;
    }

    /**
     * Decode given string using key, with pseudo base 64 vigenere cypher
     *
     * For cypher algo :
     * https://github.com/Emudofus/Dofus/blob/1.29/ank/utils/Crypt.as#L20
     *
     * @param encoded Encoded string
     *
     * @return Decoded string
     */
    public String decode(String encoded) {
        StringBuilder sb = new StringBuilder(encoded.length() / 2);

        // Iterate over pair chars
        for (int i = 0; i < encoded.length(); i += 2) {
            int k = key.charAt(i / 2) % 64; // Get key char

            // Get two chars int value (divider and modulo)
            int d = Base64.ord(encoded.charAt(i));
            int r = Base64.ord(encoded.charAt(i + 1));

            // Remove key value
            d -= k;
            r -= k;

            // if values are negative (due du modulo), reverse the module
            while (d < 0) { d += 64; }
            while (r < 0) { r += 64; }

            // retrieve the original value
            int v = d * 16 + r;

            sb.append((char) v);
        }

        return sb.toString();
    }
}
