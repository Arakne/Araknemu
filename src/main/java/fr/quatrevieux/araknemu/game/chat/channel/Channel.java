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

package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;

/**
 * Chat channel
 */
public interface Channel {
    /**
     * Get the channel type
     * Multiple channel with same type may exist. The discrimination will be performed using {@link Channel#authorized(GamePlayer)}
     */
    public ChannelType type();

    /**
     * Send a message to the channel
     *
     * @param from The sender
     * @param message The message
     */
    public void send(GamePlayer from, Message message) throws ChatException;

    /**
     * Check if the given player is authorized to send a message to the given channel
     * If this method return false, the next channel with the same type will be checked
     *
     * If no authorized channel can be found for a given {@link ChannelType},
     * a {@link ChatException} with UNAUTHORIZED error will be thrown
     *
     * @param from The player to check
     *
     * @return true if authorized
     */
    public boolean authorized(GamePlayer from);
}
