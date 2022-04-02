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

package fr.quatrevieux.araknemu.network.game.in.chat;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import org.checkerframework.common.value.qual.MinLen;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Change channel subscriptions
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Chat.as#L92
 */
public final class SubscribeChannels implements Packet {
    private final boolean subscribe;
    private final Collection<ChannelType> channels;

    public SubscribeChannels(boolean subscribe, Collection<ChannelType> channels) {
        this.subscribe = subscribe;
        this.channels = channels;
    }

    public boolean isSubscribe() {
        return subscribe;
    }

    public Collection<ChannelType> channels() {
        return channels;
    }

    public static final class Parser implements SinglePacketParser<SubscribeChannels> {
        @Override
        public SubscribeChannels parse(String input) throws ParsePacketException {
            if (input.isEmpty()) {
                throw new ParsePacketException(code(), "The packet must start with + or -");
            }

            final Collection<ChannelType> channels = EnumSet.noneOf(ChannelType.class);

            for (int i = 1; i < input.length(); ++i) {
                channels.add(
                    ChannelType.byChar(input.charAt(i))
                );
            }

            return new SubscribeChannels(
                input.charAt(0) == '+',
                channels
            );
        }

        @Override
        public @MinLen(2) String code() {
            return "cC";
        }
    }
}
