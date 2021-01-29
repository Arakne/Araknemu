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

package fr.quatrevieux.araknemu.game.item.inventory;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple implementation for {@link ItemStorage}
 */
final public class SimpleItemStorage<E extends ItemEntry> implements ItemStorage<E> {
    public interface EntryFactory<E> {
        /**
         * Create the inventory entry for the current item storage
         */
        public E create(int id, Item item, int quantity, int position);
    }

    final private Dispatcher dispatcher;
    final private EntryFactory<E> factory;

    final private Map<Integer, E> items = new HashMap<>();

    private int lastId = 0;

    public SimpleItemStorage(Dispatcher dispatcher, EntryFactory<E> factory, Collection<E> items) {
        this.dispatcher = dispatcher;
        this.factory = factory;

        for (E entry : items) {
            push(entry);

            if (entry.id() > lastId) {
                lastId = entry.id();
            }
        }
    }

    public SimpleItemStorage(Dispatcher dispatcher, EntryFactory<E> factory) {
        this(dispatcher, factory, Collections.emptyList());
    }

    @Override
    public E get(int id) throws ItemNotFoundException {
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException(id);
        }

        return items.get(id);
    }

    @Override
    public E add(Item item, int quantity, int position) {
        E entry = factory.create(++lastId, item, quantity, position);

        push(entry);
        dispatcher.dispatch(new ObjectAdded(entry));

        return entry;
    }

    @Override
    public E delete(int id) throws ItemNotFoundException {
        if (!items.containsKey(id)) {
            throw new ItemNotFoundException(id);
        }

        E entry = items.remove(id);

        dispatcher.dispatch(new ObjectDeleted(entry));

        return entry;
    }

    @Override
    public Iterator<E> iterator() {
        return items.values().iterator();
    }

    private void push(E entry) {
        items.put(entry.id(), entry);
    }
}
