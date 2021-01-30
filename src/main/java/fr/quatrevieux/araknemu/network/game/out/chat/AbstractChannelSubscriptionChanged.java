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

import java.util.Collection;

/**
 * Abstract class packet for subscription changed
 */
abstract public class AbstractChannelSubscriptionChanged {
    final private char sign;
    final private Collection<ChannelType> channels;

    public AbstractChannelSubscriptionChanged(char sign, Collection<ChannelType> channels) {
        this.sign = sign;
        this.channels = channels;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("cC");

        sb.append(sign);

        for (ChannelType type : channels) {
            sb.append(type.identifier());
        }

        return sb.toString();
    }
}
