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
 * Algorithm for hash password
 */
public interface HashAlgorithm {
    /**
     * Parse an hashed password to create {@link Password} instance
     *
     * @param hashedValue The hashed (database) value
     */
    public Password parse(String hashedValue);

    /**
     * Check if the algorithm supports (has generate) the given hashed value
     */
    public boolean supports(String hashedValue);

    /**
     * Hash a plain password
     *
     * @param inputValue Password to hash
     *
     * @return The hashed password instance
     */
    public Password hash(String inputValue);

    /**
     * Get the algorithm name
     */
    public String name();
}
