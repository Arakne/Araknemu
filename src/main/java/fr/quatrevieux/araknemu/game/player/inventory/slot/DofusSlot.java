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

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.EquipOnceConstraint;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SlotConstraint;
import org.checkerframework.common.value.qual.IntRange;

/**
 * Slot for dofus
 */
public final class DofusSlot extends AbstractWearableSlot {
    public static final @IntRange(from = 0, to = InventorySlots.SLOT_MAX) int[] SLOT_IDS = new int[] {9, 10, 11, 12, 13, 14};

    public DofusSlot(Dispatcher dispatcher, ItemStorage<InventoryEntry> storage, GamePlayer owner, InventorySlots inventorySlots, @IntRange(from = 0, to = InventorySlots.SLOT_MAX) int id) {
        super(dispatcher, storage, owner, id, SuperType.DOFUS, new SlotConstraint[] {
            new EquipOnceConstraint(inventorySlots, SLOT_IDS, false),
        });
    }
}
