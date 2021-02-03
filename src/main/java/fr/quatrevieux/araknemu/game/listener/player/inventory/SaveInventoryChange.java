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

package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.event.ItemChanged;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectMoved;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryService;

import java.util.function.Consumer;

/**
 * Save item changes into database
 */
public final class SaveInventoryChange implements EventsSubscriber {
    private final InventoryService service;

    public SaveInventoryChange(InventoryService service) {
        this.service = service;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<ObjectDeleted>() {
                @Override
                public void on(ObjectDeleted event) {
                    perform(event, service::deleteItemEntry);
                }

                @Override
                public Class<ObjectDeleted> event() {
                    return ObjectDeleted.class;
                }
            },
            new Listener<ObjectMoved>() {
                @Override
                public void on(ObjectMoved event) {
                    perform(event, service::updateItemEntry);
                }

                @Override
                public Class<ObjectMoved> event() {
                    return ObjectMoved.class;
                }
            },
            new Listener<ObjectQuantityChanged>() {
                @Override
                public void on(ObjectQuantityChanged event) {
                    perform(event, service::updateItemEntry);
                }

                @Override
                public Class<ObjectQuantityChanged> event() {
                    return ObjectQuantityChanged.class;
                }
            },
            new Listener<ObjectAdded>() {
                @Override
                public void on(ObjectAdded event) {
                    perform(event, service::saveItemEntry);
                }

                @Override
                public Class<ObjectAdded> event() {
                    return ObjectAdded.class;
                }
            },
        };
    }

    private void perform(ItemChanged event, Consumer<InventoryEntry> action) {
        final ItemEntry entry = event.entry();

        if (entry instanceof InventoryEntry) {
            action.accept((InventoryEntry) entry);
        }
    }
}
