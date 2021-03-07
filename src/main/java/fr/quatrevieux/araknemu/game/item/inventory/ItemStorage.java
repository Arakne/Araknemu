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

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Base type for store items
 */
public interface ItemStorage<E extends ItemEntry> extends Iterable<E> {
    /**
     * Get an entry by its id
     *
     * @param id The entry id
     */
    public E get(int id) throws ItemNotFoundException;

    /**
     * Add a new item to the storage
     *
     * @param item Item to add
     *
     * @return The related entry
     */
    public default E add(Item item) throws InventoryException {
        return add(item, 1, ItemEntry.DEFAULT_POSITION);
    }

    /**
     * Add a new item to the storage
     *
     * @param item Item to add
     * @param quantity Quantity to set
     *
     * @return The related entry
     */
    public default E add(Item item, int quantity) throws InventoryException {
        return add(item, quantity, ItemEntry.DEFAULT_POSITION);
    }

    /**
     * Add a new item to the storage
     *
     * @param item Item to add
     * @param quantity The quantity
     * @param position The item position
     *
     * @return The related entry
     */
    public E add(Item item, int quantity, int position) throws InventoryException;

    /**
     * Delete an entry from the storage
     *
     * @param entry The entry to delete
     *
     * @return The deleted entry. Should be same as argument
     *
     * @throws ItemNotFoundException If the entry cannot be found on the storage
     */
    public default E delete(E entry) throws InventoryException {
        return delete(entry.id());
    }

    /**
     * Delete an entry from the storage
     *
     * @param id The entry id to delete
     *
     * @return The deleted entry
     *
     * @throws ItemNotFoundException If the entry cannot be found on the storage
     */
    public E delete(int id) throws InventoryException;

    /**
     * Get a stream from the storage
     */
    public default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
