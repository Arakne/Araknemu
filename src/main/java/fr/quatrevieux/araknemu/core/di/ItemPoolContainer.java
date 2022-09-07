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

import fr.quatrevieux.araknemu.core.di.item.CachedItem;
import fr.quatrevieux.araknemu.core.di.item.ContainerItem;
import fr.quatrevieux.araknemu.core.di.item.FactoryItem;
import fr.quatrevieux.araknemu.core.di.item.ValueItem;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Container implementation using {@link fr.quatrevieux.araknemu.core.di.item.ContainerItem}
 */
public final class ItemPoolContainer implements Container {
    private final Map<Class, ContainerItem> items = new HashMap<>();

    @Override
    public <@NonNull T> T get(Class<T> type) throws ContainerException {
        final ContainerItem<@NonNull T> item = (ContainerItem<@NonNull T>) items.get(type);

        if (item == null) {
            throw new ItemNotFoundException(type);
        }

        return item.value(this);
    }

    @Override
    public boolean has(Class type) {
        return items.containsKey(type);
    }

    @Override
    public void register(ContainerModule module) {
        module.configure(new Configurator());
    }

    /**
     * Get pool configurator
     */
    public Configurator configurator() {
        return new Configurator();
    }

    /**
     * Set a new item to the container
     */
    private void set(ContainerItem item) {
        items.put(item.type(), item);
    }

    /**
     * Configuration class for {@link ItemPoolContainer}
     */
    public final class Configurator implements ContainerConfigurator {
        @Override
        public Configurator set(Object object) {
            ItemPoolContainer.this.set(new ValueItem<>(object));

            return this;
        }

        @Override
        public <@NonNull T> Configurator set(Class<T> type, @NonNull T object) {
            ItemPoolContainer.this.set(new ValueItem<>(type, object));

            return this;
        }

        @Override
        public <@NonNull T> Configurator factory(Class<T> type, FactoryItem.Factory<T> factory) {
            ItemPoolContainer.this.set(new FactoryItem<>(type, factory));

            return this;
        }

        @Override
        public <@NonNull T> Configurator persist(Class<T> type, FactoryItem.Factory<T> factory) {
            ItemPoolContainer.this.set(new CachedItem<>(
                new FactoryItem<>(type, factory)
            ));

            return this;
        }

        /**
         * Register an item to the container
         *
         * @param item Item to register
         *
         * @return this
         */
        public Configurator item(ContainerItem item) {
            ItemPoolContainer.this.set(item);

            return this;
        }
    }
}
