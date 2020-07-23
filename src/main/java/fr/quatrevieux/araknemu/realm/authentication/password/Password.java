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

/**
 * Value object for hashed password
 */
public interface Password {
    /**
     * Check the password hash
     *
     * @param input The input (plain password)
     *
     * @return true is the pass match
     */
    public boolean check(String input);

    /**
     * Does the pass need a rehash (i.e. configuration has changed, or expired)
     *
     * @return true is the password should be rehashed
     */
    public boolean needRehash();

    /**
     * Get the string value of the password
     */
    public String toString();

    /**
     * Get the hash algorithm
     */
    public HashAlgorithm algorithm();
}
