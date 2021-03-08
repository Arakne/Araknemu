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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.network.netty;

import fr.quatrevieux.araknemu.core.network.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * Adapt Netty channel to Araknemu Channel
 */
public final class ChannelAdapter implements Channel {
    private final ChannelHandlerContext channel;

    public ChannelAdapter(ChannelHandlerContext channel) {
        this.channel = channel;
    }

    @Override
    public Object id() {
        return channel.channel().id();
    }

    @Override
    public void write(Object message) {
        channel.writeAndFlush(message.toString());
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public boolean isAlive() {
        return channel.channel().isActive();
    }

    @Override
    public InetSocketAddress address() {
        return (InetSocketAddress) channel.channel().remoteAddress();
    }
}
