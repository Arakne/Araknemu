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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Store memory for AI
 */
public final class AiMemory {
    private final Map<MemoryKey<?>, Object> memory = new HashMap<>();

    /**
     * Get the value stored at the given key.
     * If the key is not present in the memory, the default value is returned.
     *
     * @param key The memory key. Should be a static instance of {@link MemoryKey}
     *
     * @return The stored value, or the default value if the key is not present
     *
     * @param <T> The value type
     *
     * @see MemoryKey#defaultValue() Called to get the default value
     */
    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(MemoryKey<T> key) {
        final T value = (T) memory.get(key);

        if (value != null) {
            return value;
        }

        final T defaultValue = key.defaultValue();

        if (defaultValue != null) {
            memory.put(key, defaultValue);
        }

        return defaultValue;
    }

    /**
     * Set a value in the memory
     *
     * @param key The key of the value. Should be a static instance of {@link MemoryKey}
     * @param value The value to store
     *
     * @param <T> The type of the value
     */
    public <T> void set(MemoryKey<T> key, @NonNull T value) {
        memory.put(key, value);
    }

    /**
     * Refresh the memory when the AI start
     * Call {@link MemoryKey#refresh(Object)} for each key in the memory
     */
    @SuppressWarnings("unchecked")
    public void refresh() {
        final Iterator<Map.Entry<MemoryKey<?>, Object>> iter = memory.entrySet().iterator();

        while (iter.hasNext()) {
            final Map.Entry<MemoryKey<?>, Object> entry = iter.next();
            final MemoryKey key = entry.getKey();
            final Object value = entry.getValue();
            final @Nullable Object newValue = key.refresh(value);

            if (newValue == null) {
                iter.remove();
            } else {
                entry.setValue(newValue);
            }
        }
    }
}
