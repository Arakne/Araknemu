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

import org.checkerframework.checker.nullness.qual.NonNull;

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

    /**
     * Create a scoped container with a single defined service
     *
     * Note: By default, the value will be mapped with it's own class
     *
     * Usage:
     * <pre>{@code
     * Foo foo = new Foo();
     *
     * Container scoped = container.with(Foo.class, FooInterface.class);
     *
     * scoped.get(Foo.class); // Get foo
     * scoped.get(FooInterface.class); // Same as above
     * scope.get(OtherClass.class); // Get the service OtherClass from the base container
     * }</pre>
     *
     * @param value The value to store
     * @param interfaces Interfaces to defined
     *
     * @param <T> The value type
     *
     * @return The scoped container
     * @see ScopedContainer
     */
    public default <T> Container with(@NonNull T value, Class<? super T>... interfaces) {
        return withAll(new ScopedContainer.Mapping<>(value, interfaces));
    }

    /**
     * Create a scoped container with multiple services
     *
     * Note: By default, the value will be mapped with it's own class
     *
     * Usage:
     * <pre>{@code
     * Foo foo = new Foo();
     *
     * Container scoped = container.withAll(
     *     new ScopedContainer.Mapping<>(Foo.class, FooInterface.class),
     *     new ScopedContainer.Mapping<>(Bar.class),
     * );
     *
     * scoped.get(Foo.class); // Get foo
     * scoped.get(FooInterface.class); // Same as above
     * scope.get(Bar.class); // Get bar
     * }</pre>
     *
     * @param mappings The interfaces / value mappings
     *
     * @return The scoped container
     * @see ScopedContainer
     */
    public default Container withAll(ScopedContainer.Mapping<?>... mappings) {
        return ScopedContainer.fromMapping(this, mappings);
    }
}
