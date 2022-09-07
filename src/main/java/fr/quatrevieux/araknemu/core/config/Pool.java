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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Pool of configuration items
 * The pool can be used as multimap (i.e. multiple values assigned to the same key) if supported by the driver
 * You can also iterate over all configuration items using iterator.
 * In case of multimap config, the iterable will return an entry for each value, with the same key.
 */
public interface Pool extends Iterable<Map.Entry<String, String>> {
    /**
     * Check if the pool has the key
     *
     * @param key Configuration item to check
     */
    public boolean has(String key);

    /**
     * Get the value of the item
     *
     * @param key The config item key
     *
     * @return Configuration value
     */
    public @Nullable String get(String key);

    /**
     * Get all values at the given key
     * In case of single value item, it will return a singleton list
     *
     * @param key The config item key
     *
     * @return Configuration values. If not set an empty list will be returned.
     */
    public List<String> getAll(String key);
}
