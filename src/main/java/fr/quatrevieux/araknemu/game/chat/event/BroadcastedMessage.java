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

package fr.quatrevieux.araknemu.game.chat.event;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Event trigger on broadcasted message sent
 */
public final class BroadcastedMessage {
    private final ChannelType channel;
    private final GamePlayer sender;
    private final String message;
    private final String extra;

    public BroadcastedMessage(ChannelType channel, GamePlayer sender, String message, String extra) {
        this.channel = channel;
        this.sender = sender;
        this.message = message;
        this.extra = extra;
    }

    public ChannelType channel() {
        return channel;
    }

    public GamePlayer sender() {
        return sender;
    }

    public String message() {
        return message;
    }

    public String extra() {
        return extra;
    }
}
