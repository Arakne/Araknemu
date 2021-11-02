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

package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.chat.event.ConcealedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;

/**
 * Channel for private messages
 */
public final class PrivateChannel implements Channel {
    private final PlayerService service;

    public PrivateChannel(PlayerService service) {
        this.service = service;
    }

    @Override
    public ChannelType type() {
        return ChannelType.PRIVATE;
    }

    @Override
    public boolean authorized(GamePlayer from) {
        return true;
    }

    @Override
    public void send(GamePlayer from, Message message) throws ChatException {
        if (!service.isOnline(message.target())) {
            throw new ChatException(ChatException.Error.USER_NOT_CONNECTED);
        }

        final GamePlayer to = service.get(message.target());
        final ConcealedMessage event = new ConcealedMessage(
            from,
            to,
            message.message(),
            message.items()
        );

        from.dispatch(event);
        to.dispatch(event);
    }
}
