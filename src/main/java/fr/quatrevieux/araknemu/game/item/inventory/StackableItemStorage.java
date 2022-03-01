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

import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * Add stack capabilities to an item storage
 */
public final class StackableItemStorage<E extends ItemEntry> implements ItemStorage<E> {
    private final ItemStorage<E> storage;
    private final int stackPosition;

    private final Map<Item, E> stackMap = new HashMap<>();

    public StackableItemStorage(ItemStorage<E> storage) {
        this(storage, ItemEntry.DEFAULT_POSITION);
    }

    public StackableItemStorage(ItemStorage<E> storage, int stackPosition) {
        this.storage = storage;
        this.stackPosition = stackPosition;

        for (E entry : storage) {
            if (entry.position() == stackPosition) {
                stackMap.put(entry.item(), entry);
            }
        }
    }

    @Override
    public E get(int id) throws ItemNotFoundException {
        return storage.get(id);
    }

    @Override
    public E add(Item item, int quantity, int position) throws InventoryException {
        if (position == stackPosition) {
            final E entry = stackMap.get(item);

            if (checkStackedEntry(entry)) {
                entry.add(quantity);

                return entry;
            }
        }

        final E entry = storage.add(item, quantity, position);

        if (position == stackPosition) {
            stackMap.put(entry.item(), entry);
        }

        return entry;
    }

    @Override
    public E delete(int id) throws InventoryException {
        final E entry = storage.delete(id);

        if (entry.position() == stackPosition) {
            stackMap.remove(entry.item());
        }

        return entry;
    }

    @Override
    public Iterator<E> iterator() {
        return storage.iterator();
    }

    /**
     * Find entry with same item
     *
     * @param item Item to search
     *
     * @return The entry, or null if there is not corresponding entry
     */
    public Optional<E> find(Item item) {
        final E entry = stackMap.get(item);

        if (!checkStackedEntry(entry)) {
            return Optional.empty();
        }

        return Optional.of(entry);
    }

    /**
     * Add item for indexing
     *
     * /!\ Use carefully : can break stacking system if overriding entry
     */
    public void indexing(E entry) {
        stackMap.put(entry.item(), entry);
    }

    /**
     * Check if the stacked entry is valid
     *
     * Issue #73 : Quantity must be checked, the entry may be deleted without remove from index
     */
    @EnsuresNonNullIf(expression = "#1", result = true)
    private boolean checkStackedEntry(@Nullable E entry) {
        return entry != null && entry.position() == stackPosition && entry.quantity() > 0;
    }
}
