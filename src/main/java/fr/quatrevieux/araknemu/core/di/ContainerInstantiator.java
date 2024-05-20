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

package fr.quatrevieux.araknemu.core.di;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Constructor;

/**
 * Default implementation of {@link Instantiator} using a {@link Container} to resolve
 * already defined services.
 *
 * When an object is not declared explicitly in the container, the instantiator will try to
 * resolve recursively the constructor arguments and instantiate them.
 *
 * This implementation will not cache instances. Unless the type is declared in the container,
 * a new instance is created each time.
 */
public final class ContainerInstantiator implements Instantiator {
    private final Container container;

    public ContainerInstantiator(Container container) {
        this.container = container;
    }

    @Override
    @SuppressWarnings("argument")
    public <T> T instantiate(Class<T> clazz) throws ContainerException {
        if (container.has(clazz)) {
            return container.get(clazz);
        }

        if (clazz.isPrimitive() || clazz.isArray() || clazz.getName().startsWith("java.")) {
            throw new InstantiatorException("Cannot instantiate primitive types or classes from java.* package");
        }

        for (Constructor<?> constructor : clazz.getConstructors()) {
            final @Nullable Object @Nullable [] parameters = resolveArguments(constructor);

            if (parameters == null) {
                continue;
            }

            try {
                return (T) constructor.newInstance(parameters);
            } catch (Exception e) {
                // Ignore and try next constructor
            }
        }

        // No constructor found : try to instantiate without constructor
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new InstantiatorException("Cannot instantiate object of type " + clazz.getName(), e);
        }

    }

    /**
     * Try to resolve constructor arguments
     * When an argument value can't be resolved, return null to indicate that the constructor can't be used
     *
     * @return Array of arguments or null when cannot instantiate the object
     */
    private @Nullable Object @Nullable [] resolveArguments(Constructor<?> constructor) {
        final Class<?>[] parametersTypes = constructor.getParameterTypes();
        final @Nullable Object[] parameters = new Object[parametersTypes.length];

        for (int i = 0; i < parameters.length; ++i) {
            try {
                parameters[i] = instantiate(parametersTypes[i]);
            } catch (ContainerException e) {
                return null;
            }
        }

        return parameters;
    }
}
