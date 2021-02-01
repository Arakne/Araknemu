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
import fr.quatrevieux.araknemu.game.item.inventory.event.KamasChanged;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectMoved;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.object.AddItem;
import fr.quatrevieux.araknemu.network.game.out.object.DestroyItem;
import fr.quatrevieux.araknemu.network.game.out.object.ItemPosition;
import fr.quatrevieux.araknemu.network.game.out.object.ItemQuantity;

/**
 * Send packets for synchronize inventory with client
 */
final public class SendInventoryUpdate implements EventsSubscriber {
    final private GamePlayer player;

    public SendInventoryUpdate(GamePlayer player) {
        this.player = player;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<KamasChanged>() {
                @Override
                public void on(KamasChanged event) {
                    send(new Stats(player.properties()));
                }

                @Override
                public Class<KamasChanged> event() {
                    return KamasChanged.class;
                }
            },
            new Listener<ObjectAdded>() {
                @Override
                public void on(ObjectAdded event) {
                    send(new AddItem(event.entry()));
                }

                @Override
                public Class<ObjectAdded> event() {
                    return ObjectAdded.class;
                }
            },
            new Listener<ObjectMoved>() {
                @Override
                public void on(ObjectMoved event) {
                    send(new ItemPosition(event.entry()));
                }

                @Override
                public Class<ObjectMoved> event() {
                    return ObjectMoved.class;
                }
            },
            new Listener<ObjectQuantityChanged>() {
                @Override
                public void on(ObjectQuantityChanged event) {
                    send(new ItemQuantity(event.entry()));
                }

                @Override
                public Class<ObjectQuantityChanged> event() {
                    return ObjectQuantityChanged.class;
                }
            },
            new Listener<ObjectDeleted>() {
                @Override
                public void on(ObjectDeleted event) {
                    send(new DestroyItem(event.entry()));
                }

                @Override
                public Class<ObjectDeleted> event() {
                    return ObjectDeleted.class;
                }
            },
        };
    }

    private void send(Object packet) {
        player.send(packet);
    }
}
