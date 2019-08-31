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

/**
 * Dependency injection container
 */
public interface Container {
    /**
     * Get instance from the container
     * The return value of get, with same type SHOULD return equals value
     * The value MAY reference to another instance
     *
     * @param type Type to find
     * @param <T> Type
     *
     * @return The contained instance
     *
     * @throws ItemNotFoundException When cannot found given type
     * @throws ContainerException When cannot instantiate the element
     */
    public <T> T get(Class<T> type) throws ContainerException;

    /**
     * Check if the container contains the type
     *
     * @param type Type to find
     *
     * @return true if the contains contains the type
     */
    public boolean has(Class type);

    /**
     * Register a module into the container
     *
     * @param module Module to register
     */
    public void register(ContainerModule module);
}
