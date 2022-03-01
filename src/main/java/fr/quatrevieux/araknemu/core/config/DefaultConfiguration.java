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

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of configuration system
 */
public final class DefaultConfiguration implements Configuration {
    private final Map<ConfigurationModule<?>, @NonNull Object> modules = new HashMap<>();

    private final Driver driver;

    public DefaultConfiguration(Driver driver) {
        this.driver = driver;
    }

    @Override
    @SuppressWarnings({"unchecked", "cast.unsafe"})
    public <C extends @NonNull Object> @NonNull C module(ConfigurationModule<C> module) {
        C config = (C) modules.get(module);

        if (config != null) {
            return config;
        }

        config = module.create(
            driver.has(module.name())
                ? driver.pool(module.name())
                : new EmptyPool()
        );

        modules.put(module, config);

        return config;
    }
}
