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
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.type.UsableItem;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.ItemClassConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SlotConstraint;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

/**
 * Slot for usable items
 */
public final class UsableSlot implements InventorySlot {
    public static final int SLOT_ID_START = 35;
    public static final int SLOT_ID_END   = 57;

    private final InventorySlot slot;

    public UsableSlot(ItemStorage<InventoryEntry> storage, int id) {
        slot = new SimpleSlot(
            id,
            new SlotConstraint[] {
                new ItemClassConstraint(UsableItem.class),
            },
            storage
        );
    }

    @Override
    public int id() {
        return slot.id();
    }

    @Override
    public Optional<InventoryEntry> entry() {
        return slot.entry();
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        slot.check(item, quantity);
    }

    @Override
    public InventoryEntry set(InventoryEntry entry) throws InventoryException {
        return slot.set(entry);
    }

    @Override
    public InventoryEntry set(Item item, int quantity) throws InventoryException {
        return slot.set(item, quantity);
    }

    @Override
    public void uncheckedSet(@Nullable InventoryEntry entry) {
        slot.uncheckedSet(entry);
    }
}
