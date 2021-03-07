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

package fr.quatrevieux.araknemu.realm;

import fr.arakne.utils.encoding.PasswordEncoder;
import fr.quatrevieux.araknemu.util.RandomStringUtil;

import java.security.SecureRandom;

/**
 * Authentication token
 */
public final class ConnectionKey {
    /**
     * The random generator for generate key
     */
    private static final RandomStringUtil rand = new RandomStringUtil(
        new SecureRandom(),
        "abcdefghijklmnopqrstuvwxyz"
    );

    private final PasswordEncoder encoder;

    public ConnectionKey(String key) {
        encoder = new PasswordEncoder(key);
    }

    /**
     * Generate a new key
     */
    public ConnectionKey() {
        this(rand.generate(32));
    }

    /**
     * Get the key value
     */
    public String key() {
        return encoder.key();
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
        return encoder.decode(encoded);
    }
}
