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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.memory;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Base type for identify a memory value on the AI
 *
 * Those objects are used as hash keys in the memory map,
 * so the implementation must be immutable and have a correct equals and hashCode methods.
 *
 * It's advised to save the instance into a constant field (or enum) to ensure that the instance will be unique,
 * and accessible from everywhere in the code.
 *
 * @param <T> The stored value type
 */
public interface MemoryKey<T> {
    /**
     * Initial value to use if the key is not present in the memory
     *
     * @see AiMemory#get(MemoryKey)
     */
    public default @Nullable T defaultValue() {
        return null;
    }

    /**
     * Refresh the value when the AI start
     *
     * @param value The last value stored in the memory
     *
     * @return The new value to store in the memory, or null to remove the key
     */
    public default @Nullable T refresh(T value) {
        return value;
    }
}
