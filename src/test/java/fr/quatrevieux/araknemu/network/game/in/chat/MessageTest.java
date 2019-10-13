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

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {
    private Message.Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Message.Parser();
    }

    @Test
    void parseInvalid() {
        assertThrows(ParsePacketException.class, () -> parser.parse("invalid"));
    }

    @Test
    void parseWithGlobalChannel() {
        Message message = parser.parse("*|My message|my items");

        assertEquals(ChannelType.MESSAGES, message.channel());
        assertEquals("My message", message.message());
        assertNull(message.target());
        assertEquals("my items", message.items());
    }

    @Test
    void parseWisp() {
        Message message = parser.parse("Bob|My message|my items");

        assertEquals(ChannelType.PRIVATE, message.channel());
        assertEquals("My message", message.message());
        assertEquals("Bob", message.target());
        assertEquals("my items", message.items());
    }

    @Test
    void parseWithoutItem() {
        Message message = parser.parse("Bob|My message|");

        assertEquals(ChannelType.PRIVATE, message.channel());
        assertEquals("My message", message.message());
        assertEquals("Bob", message.target());
        assertEquals("", message.items());
    }
}
