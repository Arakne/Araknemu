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

package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;

/**
 * Delete from database the object
 */
final public class SaveDeletedItem implements Listener<ObjectDeleted> {
    final private PlayerItemRepository repository;

    public SaveDeletedItem(PlayerItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void on(ObjectDeleted event) {
        if (!(event.entry() instanceof InventoryEntry)) {
            return;
        }

        InventoryEntry entry = (InventoryEntry) event.entry();

        repository.delete(entry.entity());
    }

    @Override
    public Class<ObjectDeleted> event() {
        return ObjectDeleted.class;
    }
}
