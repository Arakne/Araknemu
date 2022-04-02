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
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.StackableItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.value.qual.IntVal;
import org.checkerframework.dataflow.qual.Pure;

import java.util.Optional;

/**
 * Slot for default position
 *
 * This slot will handle item stacking
 */
public final class DefaultSlot implements InventorySlot {
    private final StackableItemStorage<InventoryEntry> storage;

    @SuppressWarnings("method.invocation") // id() is static
    public DefaultSlot(ItemStorage<InventoryEntry> storage) {
        this.storage = new StackableItemStorage<>(storage, id());
    }

    @Override
    @Pure
    public @IntVal(ItemEntry.DEFAULT_POSITION) int id() {
        return ItemEntry.DEFAULT_POSITION;
    }

    @Override
    public Optional<InventoryEntry> entry() {
        return Optional.empty();
    }

    @Override
    public void check(Item item, int quantity) {
        // Default slot can store all items
    }

    @Override
    public InventoryEntry set(InventoryEntry entry) throws InventoryException {
        return storage.find(entry.item())
            .map(last -> {
                storage.delete(entry);

                // Quantity = 0 should not occur
                if (entry.quantity() > 0) {
                    last.add(entry.quantity());
                }

                return last;
            })
            .orElseGet(() -> {
                // Indexing the entry after move
                // For ensure that new item will be stacked
                storage.indexing(entry);

                return entry;
            })
        ;
    }

    @Override
    public InventoryEntry set(Item item, @Positive int quantity) throws InventoryException {
        return storage.add(item, quantity, id());
    }

    @Override
    public void uncheckedSet(@Nullable InventoryEntry entry) {
        // Do not store a single item : do nothing
    }
}
