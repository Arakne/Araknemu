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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin.context;

/**
 * Base type for a context resolver which is configuration using {@link AbstractContextConfigurator}
 *
 * @param <C> The context type
 */
public interface ConfigurableContextResolver<C extends Context> extends ContextResolver {
    /**
     * Register a configurator for the current context resolver
     *
     * @return The current resolver instance
     */
    public ConfigurableContextResolver<C> register(AbstractContextConfigurator<C> configurator);
}
