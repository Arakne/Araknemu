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

package fr.quatrevieux.araknemu.game.world.creature.accessory;

import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlots;
import org.checkerframework.common.value.qual.IntRange;

import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * List type of accessories
 */
public enum AccessoryType {
    WEAPON(1),
    HELMET(6),
    MANTLE(7),
    PET(8),
    SHIELD(15);

    private static final Map<Integer, AccessoryType> typesBySlot = Arrays
        .stream(values())
        .collect(Collectors.toMap(AccessoryType::slot, Function.identity()))
    ;

    private static final int[] SLOT_IDS = Arrays.stream(values()).mapToInt(AccessoryType::slot).toArray();

    private final @IntRange(from = 0, to = InventorySlots.SLOT_MAX) int slot;

    AccessoryType(@IntRange(from = 0, to = InventorySlots.SLOT_MAX) int slot) {
        this.slot = slot;
    }

    /**
     * Get the related inventory slot
     */
    public @IntRange(from = 0, to = InventorySlots.SLOT_MAX) int slot() {
        return slot;
    }

    /**
     * Get the type by slot id
     *
     * @return The accessory type or null if no accessory is related to the slot
     */
    public static AccessoryType bySlot(int slot) {
        final AccessoryType type = typesBySlot.get(slot);

        if (type != null) {
            return type;
        }

        throw new NoSuchElementException("Invalid slot " + slot + " for accessory");
    }

    /**
     * Get accessories slot ids
     */
    public static int[] slots() {
        return SLOT_IDS;
    }

    /**
     * Check if the slot is an accessory
     */
    public static boolean isAccessorySlot(int slotId) {
        return typesBySlot.containsKey(slotId);
    }
}
