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

package fr.quatrevieux.araknemu.game.listener.player.exchange;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.item.inventory.event.KamasChanged;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.world.util.Sender;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage.StorageKamas;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage.StorageObject;

/**
 * Send packets for storage's change
 */
public final class SendStoragePackets implements EventsSubscriber {
    private final Sender output;

    public SendStoragePackets(Sender output) {
        this.output = output;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<ObjectAdded>() {
                @Override
                public void on(ObjectAdded event) {
                    output.send(new StorageObject(event.entry()));
                }

                @Override
                public Class<ObjectAdded> event() {
                    return ObjectAdded.class;
                }
            },

            new Listener<ObjectDeleted>() {
                @Override
                public void on(ObjectDeleted event) {
                    output.send(new StorageObject(event.entry()));
                }

                @Override
                public Class<ObjectDeleted> event() {
                    return ObjectDeleted.class;
                }
            },

            new Listener<ObjectQuantityChanged>() {
                @Override
                public void on(ObjectQuantityChanged event) {
                    output.send(new StorageObject(event.entry()));
                }

                @Override
                public Class<ObjectQuantityChanged> event() {
                    return ObjectQuantityChanged.class;
                }
            },

            new Listener<KamasChanged>() {
                @Override
                public void on(KamasChanged event) {
                    output.send(new StorageKamas(event.newQuantity()));
                }

                @Override
                public Class<KamasChanged> event() {
                    return KamasChanged.class;
                }
            },
        };
    }
}
