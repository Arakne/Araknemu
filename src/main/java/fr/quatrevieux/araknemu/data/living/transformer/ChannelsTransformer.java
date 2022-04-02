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

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

import java.util.EnumSet;
import java.util.Set;

/**
 * Transformer for subscribed channels
 */
public final class ChannelsTransformer implements Transformer<Set<ChannelType>> {
    @Override
    public @NonNull String serialize(@PolyNull Set<ChannelType> value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();

        for (ChannelType channel : value) {
            sb.append(channel.identifier());
        }

        return sb.toString();
    }

    @Override
    public @NonNull Set<ChannelType> unserialize(@PolyNull String serialize) {
        final Set<ChannelType> channels = EnumSet.noneOf(ChannelType.class);

        if (serialize == null || serialize.isEmpty()) {
            return channels;
        }

        for (int i = 0; i < serialize.length(); ++i) {
            channels.add(
                ChannelType.byChar(serialize.charAt(i))
            );
        }

        return channels;
    }
}
