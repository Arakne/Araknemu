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
import fr.quatrevieux.araknemu.core.di.ContainerException;

/**
 * Container item using a factory to create de instance
 *
 * @param <T> Item type
 */
public final class FactoryItem<T> implements ContainerItem<T> {
    public interface Factory<T> {
        /**
         * Instantiate the value
         *
         * @param container The DI container instance
         *
         * @return The new instance
         */
        public T make(Container container) throws ContainerException;
    }

    private final Class<T> type;
    private final Factory<T> factory;

    public FactoryItem(Class<T> type, Factory<T> factory) {
        this.type = type;
        this.factory = factory;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public T value(Container container) throws ContainerException {
        return factory.make(container);
    }
}
