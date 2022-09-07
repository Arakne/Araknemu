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

import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class SubscribeChannelsTest {
    @Test
    void parseSingleChannel() {
        SubscribeChannels.Parser parser = new SubscribeChannels.Parser();

        SubscribeChannels sub = parser.parse("+i");

        assertTrue(sub.isSubscribe());
        assertEquals(EnumSet.of(ChannelType.INFO), sub.channels());
    }

    @Test
    void parseUnsubscribe() {
        SubscribeChannels.Parser parser = new SubscribeChannels.Parser();

        SubscribeChannels sub = parser.parse("-i");

        assertFalse(sub.isSubscribe());
        assertEquals(EnumSet.of(ChannelType.INFO), sub.channels());
    }

    @Test
    void parseMultipleChannels() {
        SubscribeChannels.Parser parser = new SubscribeChannels.Parser();

        SubscribeChannels sub = parser.parse("+#$p");

        assertTrue(sub.isSubscribe());
        assertEquals(EnumSet.of(
            ChannelType.FIGHT_TEAM,
            ChannelType.GROUP,
            ChannelType.PRIVATE
        ), sub.channels());
    }

    @Test
    void parseInvalid() {
        SubscribeChannels.Parser parser = new SubscribeChannels.Parser();

        assertThrows(ParsePacketException.class, () -> parser.parse(""));
    }
}
