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

package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.type.AbstractEquipment;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;

import java.util.Optional;

/**
 * Slot for player inventory
 */
public interface InventorySlot {
    /**
     * The slot id
     */
    public int id();

    /**
     * Get the current entry
     */
    public Optional<InventoryEntry> entry();

    /**
     * Check if the item can be moved to this slot
     *
     * @param item Item to set
     * @param quantity Quantity to set
     */
    public void check(Item item, int quantity) throws InventoryException;

    /**
     * Set an entry to the slot
     *
     * This method will be called after a {@link InventoryEntry#move(int, int)}
     */
    public InventoryEntry set(InventoryEntry entry) throws InventoryException;

    /**
     * Set a new item to the slot
     *
     * This method will be called after an {@link fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory#add(Item)}
     */
    public InventoryEntry set(Item item, int quantity) throws InventoryException;

    /**
     * Set the entry to the slot without do any checks
     */
    public void uncheckedSet(InventoryEntry entry);

    /**
     * Remove the current entry
     */
    default public void unset() {
        uncheckedSet(null);
    }

    /**
     * Get the current equipment
     */
    default public Optional<AbstractEquipment> equipment() {
        return Optional.empty();
    }
}
