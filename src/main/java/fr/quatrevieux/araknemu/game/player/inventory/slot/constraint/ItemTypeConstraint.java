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
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;

/**
 * Check the item type
 */
public final class ItemTypeConstraint implements SlotConstraint {
    private final SuperType type;

    public ItemTypeConstraint(SuperType type) {
        this.type = type;
    }

    @Override
    public void check(Item item, int quantity) throws InventoryException {
        if (item.type().superType() != type) {
            throw new InventoryException("Bad item type");
        }
    }
}
