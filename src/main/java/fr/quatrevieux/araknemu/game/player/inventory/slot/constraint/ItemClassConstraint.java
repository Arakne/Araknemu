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

package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;

/**
 * Check for the item class
 */
public final class ItemClassConstraint implements SlotConstraint {
    private final Class<? extends Item> type;

    public ItemClassConstraint(Class<? extends Item> type) {
        this.type = type;
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        if (!type.isInstance(item)) {
            throw new InventoryException("Bad item class");
        }
    }
}
