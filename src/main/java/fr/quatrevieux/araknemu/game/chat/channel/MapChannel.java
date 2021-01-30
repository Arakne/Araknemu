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
import fr.quatrevieux.araknemu.game.chat.event.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.exploration.creature.operation.DispatchEvent;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.info.Information;

/**
 * The default chat channel : send to all map
 */
final public class MapChannel implements Channel {
    @Override
    public ChannelType type() {
        return ChannelType.MESSAGES;
    }

    @Override
    public void send(GamePlayer from, Message message) {
        if (!message.items().isEmpty()) {
            from.send(Information.cannotPostItemOnChannel());
            return;
        }

        final BroadcastedMessage event = new BroadcastedMessage(
            type(),
            from,
            message.message(),
            ""
        );

        if (from.isExploring()) {
            from.exploration().map().apply(new DispatchEvent(event));
        }

        if (from.isFighting()) {
            from
                .fighter()
                .fight()
                .fighters()
                .forEach(player -> player.dispatch(event))
            ;
        }
    }
}
