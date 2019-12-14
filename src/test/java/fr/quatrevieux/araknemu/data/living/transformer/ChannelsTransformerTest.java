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

package fr.quatrevieux.araknemu.data.living.transformer;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class ChannelsTransformerTest {
    private ChannelsTransformer transformer = new ChannelsTransformer();

    @Test
    void serializeNull() {
        assertEquals("", transformer.serialize(null));
    }

    @Test
    void serializeEmptySet() {
        assertEquals("", transformer.serialize(EnumSet.noneOf(ChannelType.class)));
    }

    @Test
    void serialize() {
        assertEquals("i*:", transformer.serialize(EnumSet.of(ChannelType.INFO, ChannelType.MESSAGES, ChannelType.TRADE)));
    }

    @Test
    void unserializeNull() {
        assertEquals(EnumSet.noneOf(ChannelType.class), transformer.unserialize(null));
    }

    @Test
    void unserialize() {
        assertEquals(EnumSet.of(ChannelType.INFO, ChannelType.MESSAGES, ChannelType.TRADE), transformer.unserialize("i*:"));
    }
}
