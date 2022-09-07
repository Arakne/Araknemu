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

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlots;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.common.value.qual.IntRange;

import java.util.List;

/**
 * Entry for inventory
 */
public interface ItemEntry {
    public static final int DEFAULT_POSITION = -1;

    /**
     * Get the entry id
     * This value is unique over the inventory
     */
    public int id();

    /**
     * Get the entry position
     *
     * @see ItemEntry#isDefaultPosition() To check if the entry is on default position
     */
    public @IntRange(from = -1, to = InventorySlots.SLOT_MAX) int position();

    /**
     * Check if the entry is on the default position
     *
     * @see ItemEntry#position() For get the real position
     */
    public default boolean isDefaultPosition() {
        return position() == DEFAULT_POSITION;
    }

    /**
     * Get the related item
     */
    public Item item();

    /**
     * Get the item quantity
     */
    public @NonNegative int quantity();

    /**
     * Add the quantity to the entry
     *
     * @param quantity The quantity to add. Must be a strictly positive integer
     *
     * @throws InventoryException When invalid quantity given
     */
    public void add(@Positive int quantity);

    /**
     * Remove the quantity to the entry
     *
     * @param quantity The quantity to remove. Must be a strictly positive integer, less or equals than current quantity
     *
     * @throws InventoryException When invalid quantity given
     */
    public void remove(@Positive int quantity) throws InventoryException;

    /**
     * Export the item effects
     */
    public List<ItemTemplateEffectEntry> effects();

    /**
     * Get the template id
     */
    public int templateId();
}
