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
 * Create a configuration object related to the configuration pool
 * The instance of the configuration should be cached, and used as a singleton / static final constant
 *
 * @param <C> The configuration class
 */
public interface ConfigurationModule<C> {
    /**
     * Create the configuration object from the pool
     */
    public C create(Pool pool);

    /**
     * Get the module name
     * This name is used as configuration section
     */
    public String name();
}
