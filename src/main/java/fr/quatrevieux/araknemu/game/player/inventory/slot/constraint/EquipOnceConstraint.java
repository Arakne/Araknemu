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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.AlreadyEquippedException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlots;
import org.checkerframework.common.value.qual.IntRange;

/**
 * Check that the item can be equipped only once
 * This constraint will raise {@link AlreadyEquippedException} if the item is already equipped on another slot
 */
public final class EquipOnceConstraint implements SlotConstraint {
    private final InventorySlots inventorySlots;
    private final @IntRange(from = -1, to = InventorySlots.SLOT_MAX) int[] slotIds;
    private final boolean onlyWithinItemSet;

    /**
     * @param inventorySlots the player inventory
     * @param slotIds the slots ids to check
     * @param onlyWithinItemSet if true, only check items that are in an item set (use for rings)
     */
    public EquipOnceConstraint(InventorySlots inventorySlots, @IntRange(from = -1, to = InventorySlots.SLOT_MAX) int[] slotIds, boolean onlyWithinItemSet) {
        this.inventorySlots = inventorySlots;
        this.slotIds = slotIds;
        this.onlyWithinItemSet = onlyWithinItemSet;
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        if (onlyWithinItemSet && !item.set().isPresent()) {
            return;
        }

        for (int slot : slotIds) {
            if (inventorySlots.get(slot).entry().filter(entry -> item.template().equals(entry.item().template())).isPresent()) {
                throw new AlreadyEquippedException();
            }
        }
    }
}
