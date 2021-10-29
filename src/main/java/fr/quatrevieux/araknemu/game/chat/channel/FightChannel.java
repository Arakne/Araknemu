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
import fr.quatrevieux.araknemu.game.chat.event.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.info.Information;

/**
 * Send a message to all fighters
 * The message is also sent to spectators
 */
public final class FightChannel implements Channel {
    @Override
    public ChannelType type() {
        return ChannelType.MESSAGES;
    }

    @Override
    public boolean authorized(GamePlayer from) {
        return from.isFighting();
    }

    @Override
    public void send(GamePlayer from, Message message) {
        if (!message.items().isEmpty()) {
            from.send(Information.cannotPostItemOnChannel());
            return;
        }

        from.fighter().fight().dispatchToAll(new BroadcastedMessage(type(), from, message.message(), ""));
    }
}
