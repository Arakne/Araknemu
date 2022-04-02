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

package fr.quatrevieux.araknemu.core.di;

import fr.quatrevieux.araknemu.core.di.item.FactoryItem;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 */
public interface ContainerConfigurator {
    /**
     * Register an instance to the container
     *
     * @param object Instance to register
     *
     * @return this
     */
    public ContainerConfigurator set(Object object);

    /**
     * Register an instance to the container, at the given type
     *
     * @param type Type to register
     * @param object Instance to register
     *
     * @return this
     */
    public <@NonNull T> ContainerConfigurator set(Class<T> type, @NonNull T object);

    /**
     * Add a factory to the container
     *
     * @param type Type to register
     * @param factory The factory
     *
     * @return this
     */
    public <@NonNull T> ContainerConfigurator factory(Class<T> type, FactoryItem.Factory<T> factory);

    /**
     * Add a factory to the container and persist (i.e. cache) the value
     *
     * @param type Type to register
     * @param factory The factory
     *
     * @return this
     */
    public <@NonNull T> ContainerConfigurator persist(Class<T> type, FactoryItem.Factory<T> factory);
}
