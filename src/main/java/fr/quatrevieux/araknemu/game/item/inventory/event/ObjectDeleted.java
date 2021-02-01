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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.item.inventory.event;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * Event sent when an object is removed from an {@link fr.quatrevieux.araknemu.game.item.inventory.ItemStorage}
 *
 * @see fr.quatrevieux.araknemu.game.item.inventory.ItemStorage#delete(int)
 */
public final class ObjectDeleted implements ItemChanged {
    private final ItemEntry entry;

    public ObjectDeleted(ItemEntry entry) {
        this.entry = entry;
    }

    @Override
    public ItemEntry entry() {
        return entry;
    }
}
