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

package fr.quatrevieux.araknemu.game.player.inventory.accessory;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessory;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;

/**
 * Adapt inventory entry to accessory
 */
public final class InventoryAccessory implements Accessory {
    private final ItemEntry entry;

    public InventoryAccessory(ItemEntry entry) {
        this.entry = entry;
    }

    @Override
    public AccessoryType type() {
        return AccessoryType.bySlot(entry.position());
    }

    @Override
    public int appearance() {
        return entry.templateId();
    }

    @Override
    public String toString() {
        return Integer.toHexString(appearance());
    }
}
