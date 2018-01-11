package fr.quatrevieux.araknemu.network.adapter.netty;

import fr.quatrevieux.araknemu.network.adapter.Session;
import fr.quatrevieux.araknemu.network.adapter.SessionHandler;
import io.netty.channel.*;
import io.netty.util.AttributeKey;

/**
 * Adapt Netty ChannelInboundHandler to SessionHandler
 */
@ChannelHandler.Sharable
final public class SessionHandlerAdapter<S extends Session> extends ChannelInboundHandlerAdapter {
    final private AttributeKey<S> sessionAttribute = AttributeKey.valueOf("session");

    final private SessionHandler<S> handler;

    public SessionHandlerAdapter(SessionHandler<S> handler) {
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        S session = handler.create(
            new ChannelAdapter(ctx)
        );

        ctx
            .channel()
            .attr(sessionAttribute)
            .set(session)
        ;

        handler.opened(session);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        handler.closed(
            ctx.channel().attr(sessionAttribute).get()
        );
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        handler.received(
            ctx.channel().attr(sessionAttribute).get(),
            msg.toString()
        );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        handler.exception(
            ctx.channel().attr(sessionAttribute).get(),
            cause
        );
    }

    AttributeKey<S> sessionAttribute() {
        return sessionAttribute;
    }
}
