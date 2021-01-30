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

import java.util.Random;

/**
 * Random generation for strings
 */
final public class RandomStringUtil {
    final private Random random;
    final private CharSequence charset;

    /**
     * Create instance
     * @param random The random generator instance
     * @param charset  The charset to use
     */
    public RandomStringUtil(Random random, CharSequence charset) {
        this.random = random;
        this.charset = charset;
    }

    /**
     * Generate the random string
     *
     * @param length The required string length
     */
    public String generate(int length) {
        char[] buffer = new char[length];

        for (int i = 0; i < length; ++i) {
            buffer[i] = charset.charAt(random.nextInt(charset.length()));
        }

        return new String(buffer);
    }
}
