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

package fr.quatrevieux.araknemu.network.game.out.chat;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AbstractChannelSubscriptionChangedTest {
    class Impl extends AbstractChannelSubscriptionChanged {
        public Impl(char sign, Collection<ChannelType> channels) {
            super(sign, channels);
        }
    }

    @Test
    void withOneChannel() {
        assertEquals(
            "cC+@",
            new Impl('+', Collections.singleton(ChannelType.ADMIN)).toString()
        );
    }

    @Test
    void withMultipleChannels() {
        assertEquals(
            "cC-ip*",
            new Impl(
                '-',
                Arrays.asList(ChannelType.INFO, ChannelType.PRIVATE, ChannelType.MESSAGES)
            ).toString()
        );
    }
}
