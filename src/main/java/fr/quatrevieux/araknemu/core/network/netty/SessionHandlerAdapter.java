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

import fr.quatrevieux.araknemu.core.network.SessionClosed;
import fr.quatrevieux.araknemu.core.network.SessionCreated;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.core.network.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Adapt Netty ChannelInboundHandler to SessionHandler
 */
@ChannelHandler.Sharable
public final class SessionHandlerAdapter<S extends Session> extends ChannelInboundHandlerAdapter {
    private final AttributeKey<S> sessionAttribute = AttributeKey.valueOf("session");

    private final SessionFactory<S> factory;
    private final ConcurrentMap<ChannelId, S> sessions = new ConcurrentHashMap<>();

    public SessionHandlerAdapter(SessionFactory<S> factory) {
        this.factory = factory;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final S session = factory.create(new ChannelAdapter(ctx));

        ctx
            .channel()
            .attr(sessionAttribute)
            .set(session)
        ;

        sessions.put(ctx.channel().id(), session);
        session.receive(new SessionCreated());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        try {
            ctx.channel().attr(sessionAttribute).get().receive(new SessionClosed());
        } finally {
            sessions.remove(ctx.channel().id());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.channel().attr(sessionAttribute).get().receive(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.channel().attr(sessionAttribute).get().exception(cause);
    }

    /**
     * Get all active sessions
     */
    public Collection<S> sessions() {
        return sessions.values();
    }
}
