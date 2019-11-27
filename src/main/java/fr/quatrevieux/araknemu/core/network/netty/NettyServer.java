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

package fr.quatrevieux.araknemu.core.network.netty;

import fr.quatrevieux.araknemu.core.network.Server;
import fr.quatrevieux.araknemu.core.network.session.SessionFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * Server adapter for Netty
 */
final public class NettyServer implements Server {
    @ChannelHandler.Sharable
    final static public class MessageEndEncoder extends MessageToMessageEncoder {
        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, List out) {
            out.add(msg + "\000");
        }
    }

    final private SessionFactory factory;
    final private int port;
    final private int readTimeout;

    private Channel serverChannel;
    private EventLoopGroup loopGroup;

    public NettyServer(SessionFactory factory, int port, int readTimeout) {
        this.factory = factory;
        this.port = port;
        this.readTimeout = readTimeout;
    }

    @Override
    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();

        SessionHandlerAdapter handlerAdapter = new SessionHandlerAdapter(factory);
        StringDecoder decoder = new StringDecoder(CharsetUtil.UTF_8);
        StringEncoder encoder = new StringEncoder(CharsetUtil.UTF_8);
        MessageToMessageEncoder messageEncoder = new MessageEndEncoder();

        bootstrap
            .group(loopGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()))
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer() {
                protected void initChannel(Channel channel) {
                    channel
                        .pipeline()
                        .addLast(new DelimiterBasedFrameDecoder(4096, Unpooled.wrappedBuffer(new byte[]{10, 0})))
                        .addLast(encoder)
                        .addLast(decoder)
                        .addLast(messageEncoder)
                        .addLast(new ReadTimeoutHandler(readTimeout))
                        .addLast(handlerAdapter)
                    ;
                }
            })
        ;

        serverChannel = bootstrap
            .localAddress(port)
            .bind()
            .channel()
        ;
    }

    @Override
    public void stop() throws Exception {
        serverChannel.closeFuture();
        loopGroup.shutdownGracefully().sync();
    }
}
