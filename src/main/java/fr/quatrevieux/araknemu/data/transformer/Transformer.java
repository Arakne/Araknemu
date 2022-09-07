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

package fr.quatrevieux.araknemu.data.transformer;

import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * Interface for data transformer
 *
 * @param <T> The value type to transform
 */
public interface Transformer<T> {
    /**
     * Serialize the value to a simple string
     *
     * If the value is null, the method should return null
     */
    public @PolyNull String serialize(@PolyNull T value);

    /**
     * Parse the serialized string to the original object value
     *
     * If the value is null, the method should return null
     *
     * @throws TransformerException When invalid serialized data is given
     */
    public @PolyNull T unserialize(@PolyNull String serialize) throws TransformerException;
}
