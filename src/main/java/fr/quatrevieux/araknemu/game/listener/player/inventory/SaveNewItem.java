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
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;

/**
 * Save to database the new object
 */
final public class SaveNewItem implements Listener<ObjectAdded> {
    final private PlayerItemRepository repository;

    public SaveNewItem(PlayerItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public void on(ObjectAdded event) {
        if (!(event.entry() instanceof InventoryEntry)) {
            return;
        }

        InventoryEntry entry = (InventoryEntry) event.entry();

        repository.add(entry.entity());
    }

    @Override
    public Class<ObjectAdded> event() {
        return ObjectAdded.class;
    }
}
