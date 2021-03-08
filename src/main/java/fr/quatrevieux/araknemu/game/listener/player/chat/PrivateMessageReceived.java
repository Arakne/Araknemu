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

package fr.quatrevieux.araknemu.game.listener.player.chat;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.chat.event.ConcealedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.chat.PrivateMessage;

/**
 * Listen private messages
 */
public final class PrivateMessageReceived implements Listener<ConcealedMessage> {
    private final GamePlayer player;

    public PrivateMessageReceived(GamePlayer player) {
        this.player = player;
    }

    @Override
    public void on(ConcealedMessage event) {
        if (player == event.sender()) {
            player.send(
                new PrivateMessage(
                    PrivateMessage.TYPE_TO,
                    event.receiver(),
                    event.message(),
                    event.extra()
                )
            );
        } else if (player == event.receiver()) {
            player.send(
                new PrivateMessage(
                    PrivateMessage.TYPE_FROM,
                    event.sender(),
                    event.message(),
                    event.extra()
                )
            );
        }
    }

    @Override
    public Class<ConcealedMessage> event() {
        return ConcealedMessage.class;
    }
}
