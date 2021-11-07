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
import fr.quatrevieux.araknemu.game.chat.event.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.info.Information;

/**
 * Spectator channel : send message to all spectators of the fight
 * Note: the same channel is used on both default and fight team types
 */
public final class FightSpectatorChannel implements Channel {
    private final ChannelType type;

    public FightSpectatorChannel(ChannelType type) {
        this.type = type;
    }

    @Override
    public ChannelType type() {
        return type;
    }

    @Override
    public boolean authorized(GamePlayer from) {
        return from.isSpectator();
    }

    @Override
    public void send(GamePlayer from, Message message) throws ChatException {
        if (!message.items().isEmpty()) {
            from.send(Information.cannotPostItemOnChannel());
            return;
        }

        // Hardcoded to FIGHT_TEAM because the message must be sent to this channel even if it's send on MESSAGES type
        final BroadcastedMessage event = new BroadcastedMessage(ChannelType.FIGHT_TEAM, from, message.message(), "");

        from.spectator().fight().spectators().dispatch(event);
    }
}
