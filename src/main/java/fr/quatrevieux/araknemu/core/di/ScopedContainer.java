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

package fr.quatrevieux.araknemu.core.di;

import java.util.HashMap;
import java.util.Map;

/**
 * Container with manually defined services as scope
 *
 * Usage:
 * <pre>{@code
 * Container container = ...;
 * Container scoped = container.with(myService, MyServiceInterface.class);
 *
 * scoped.get(MyServiceInterface.class); // Will return myService
 * }</pre>
 */
public final class ScopedContainer implements Container {
    private final Container container;
    private final Map<Class, Object> scope;

    private ScopedContainer(Container container, Map<Class, Object> scope) {
        this.container = container;
        this.scope = scope;
    }

    @Override
    public <T> T get(Class<T> type) throws ContainerException {
        if (scope.containsKey(type)) {
            return (T) scope.get(type);
        }

        return container.get(type);
    }

    @Override
    public boolean has(Class type) {
        return scope.containsKey(type) || container.has(type);
    }

    @Override
    public void register(ContainerModule module) {
        throw new UnsupportedOperationException("Cannot register a module on a scoped container");
    }

    /**
     * Create the scoped container from the given mapping
     *
     * @param container Base container
     * @param mappings The mapping
     *
     * @return The created container instance
     */
    public static ScopedContainer fromMapping(Container container, Mapping[] mappings) {
        final Map<Class, Object> scope = new HashMap<>();

        for (Mapping mapping : mappings) {
            scope.put(mapping.value.getClass(), mapping.value);

            for (Class type : mapping.interfaces) {
                scope.put(type, mapping.value);
            }
        }

        return new ScopedContainer(container, scope);
    }

    /**
     * An interfaces / value mapping
     *
     * @param <T> The base type
     */
    public static final class Mapping<T> {
        private final T value;
        private final Class[] interfaces;

        public Mapping(T value, Class<? super T>... interfaces) {
            this.value = value;
            this.interfaces = interfaces;
        }
    }
}
