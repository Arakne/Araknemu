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

package fr.quatrevieux.araknemu.core.di.item;

import fr.quatrevieux.araknemu.core.di.Container;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Simple value container item
 *
 * @param <T> The value type
 */
public final class ValueItem<T extends @NonNull Object> implements ContainerItem<T> {
    private final Class<T> type;
    private final @NonNull T value;

    public ValueItem(Class<T> type, @NonNull T value) {
        this.type = type;
        this.value = value;
    }

    public ValueItem(@NonNull T value) {
        this((Class<T>) value.getClass(), value);
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public @NonNull T value(Container container) {
        return value;
    }
}
