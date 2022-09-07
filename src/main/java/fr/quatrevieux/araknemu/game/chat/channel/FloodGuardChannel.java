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

import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.info.Information;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Decorate channel for adding flood guard
 */
public final class FloodGuardChannel implements Channel {
    private final Channel channel;
    private final GameConfiguration.ChatConfiguration configuration;

    private final ConcurrentMap<Integer, Long> lastSentTime = new ConcurrentHashMap<>();

    public FloodGuardChannel(Channel channel, GameConfiguration.ChatConfiguration configuration) {
        this.channel = channel;
        this.configuration = configuration;
    }

    @Override
    public ChannelType type() {
        return channel.type();
    }

    @Override
    public boolean authorized(GamePlayer from) {
        return channel.authorized(from);
    }

    @Override
    public void send(GamePlayer from, Message message) throws ChatException {
        if (!checkFlood(from)) {
            return;
        }

        channel.send(from, message);
        lastSentTime.put(from.id(), System.currentTimeMillis());
    }

    /**
     * Check for flood timer
     *
     * @param sender The message sender
     *
     * @return true if can sent the message
     */
    private boolean checkFlood(GamePlayer sender) {
        if (configuration.floodTime() < 0) {
            return true;
        }

        final Long last = lastSentTime.get(sender.id());

        if (last == null) {
            return true;
        }

        final int remainingTime = (int) ((last + configuration.floodTime() * 1000 - System.currentTimeMillis()) / 1000);

        if (remainingTime <= 0) {
            return true;
        }

        sender.send(Information.chatFlood(remainingTime));
        return false;
    }
}
