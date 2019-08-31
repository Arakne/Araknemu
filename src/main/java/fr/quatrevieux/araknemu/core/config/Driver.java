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

package fr.quatrevieux.araknemu.core.config;

/**
 *
 */
public interface Driver {
    /**
     * Check if the configuration has the key
     * @param key Configuration item to check
     */
    public boolean has(String key);

    /**
     * Get the value of the item
     * @param key The config item key
     * @return The raw value
     */
    public Object get(String key);

    /**
     * Get a pool of config values
     * @param key The pool key
     * @return The pool of values
     */
    public Pool pool(String key);
}
