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

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.event.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.network.game.out.object.InventoryWeight;

/**
 * Send inventory weight when inventory operations or characteristics changes occurs
 */
public final class SendWeight implements EventsSubscriber {
    private final GamePlayer player;

    public SendWeight(GamePlayer player) {
        this.player = player;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<GameJoined>() {
                @Override
                public void on(GameJoined event) {
                    send();
                }

                @Override
                public Class<GameJoined> event() {
                    return GameJoined.class;
                }
            },
            new Listener<CharacteristicsChanged>() {
                @Override
                public void on(CharacteristicsChanged event) {
                    send();
                }

                @Override
                public Class<CharacteristicsChanged> event() {
                    return CharacteristicsChanged.class;
                }
            },
            new Listener<ObjectQuantityChanged>() {
                @Override
                public void on(ObjectQuantityChanged event) {
                    send();
                }

                @Override
                public Class<ObjectQuantityChanged> event() {
                    return ObjectQuantityChanged.class;
                }
            },
            new Listener<ObjectAdded>() {
                @Override
                public void on(ObjectAdded event) {
                    send();
                }

                @Override
                public Class<ObjectAdded> event() {
                    return ObjectAdded.class;
                }
            },
            new Listener<ObjectDeleted>() {
                @Override
                public void on(ObjectDeleted event) {
                    send();
                }

                @Override
                public Class<ObjectDeleted> event() {
                    return ObjectDeleted.class;
                }
            },
        };
    }

    private void send() {
        player.inventory().refreshWeight();
        player.send(new InventoryWeight(player));
    }
}
