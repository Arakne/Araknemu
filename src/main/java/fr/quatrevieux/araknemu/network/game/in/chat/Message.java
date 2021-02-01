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
import org.apache.commons.lang3.StringUtils;

/**
 * Message sent to chat
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Chat.as#L19
 */
public final class Message implements Packet {
    public static final class Parser implements SinglePacketParser<Message> {
        @Override
        public Message parse(String input) throws ParsePacketException {
            final String[] parts = StringUtils.split(input, "|", 3);

            if (parts.length < 2) {
                throw new ParsePacketException("BM" + input, "Needs at least 2 parts");
            }

            final String extra = parts.length == 3 ? parts[2] : "";

            if (parts[0].length() == 1) {
                return new Message(
                    ChannelType.byChar(parts[0].charAt(0)),
                    null,
                    parts[1],
                    extra
                );
            }

            return new Message(
                ChannelType.PRIVATE,
                parts[0],
                parts[1],
                extra
            );
        }

        @Override
        public String code() {
            return "BM";
        }
    }

    private final ChannelType channel;
    private final String target;
    private final String message;
    private final String items;

    public Message(ChannelType channel, String target, String message, String items) {
        this.channel = channel;
        this.target = target;
        this.message = message;
        this.items = items;
    }

    public ChannelType channel() {
        return channel;
    }

    /**
     * The target is null when send to a global chat
     */
    public String target() {
        return target;
    }

    public String message() {
        return message;
    }

    public String items() {
        return items;
    }
}
