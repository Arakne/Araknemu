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

package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Send message to client
 *
 * @todo Escape message
 */
final public class MessageSent {
    final private GamePlayer sender;
    final private ChannelType channel;
    final private String message;
    final private String extra;

    public MessageSent(GamePlayer sender, ChannelType channel, String message, String extra) {
        this.sender = sender;
        this.channel = channel;
        this.message = message;
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "cMK" + channel.identifier() + "|" + sender.id() + "|" + sender.name() + "|" + message + "|" + extra;
    }
}
