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
import fr.quatrevieux.araknemu.game.chat.event.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;

import java.util.function.Predicate;

/**
 * Channels which send to all online players
 */
final public class GlobalChannel implements Channel {
    final private ChannelType channel;
    final private Predicate<GamePlayer> filter;
    final private PlayerService service;

    public GlobalChannel(ChannelType channel, Predicate<GamePlayer> filter, PlayerService service) {
        this.channel = channel;
        this.filter = filter;
        this.service = service;
    }

    public GlobalChannel(ChannelType channel, PlayerService service) {
        this(channel, player -> true, service);
    }

    @Override
    public ChannelType type() {
        return channel;
    }

    @Override
    public void send(GamePlayer from, Message message) throws ChatException {
        if (!filter.test(from)) {
            throw new ChatException(ChatException.Error.UNAUTHORIZED);
        }

        final BroadcastedMessage event = new BroadcastedMessage(
            type(),
            from,
            message.message(),
            message.items()
        );

        service
            .filter(filter)
            .forEach(player -> player.dispatch(event))
        ;
    }
}
