package fr.quatrevieux.araknemu.network.adapter.netty;

import fr.quatrevieux.araknemu.network.adapter.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

/**
 * Adapt Netty channel to Arakanemu Channel
 */
final public class ChannelAdapter implements Channel {
    final private ChannelHandlerContext channel;

    public ChannelAdapter(ChannelHandlerContext channel) {
        this.channel = channel;
    }

    @Override
    public long id() {
        //channel.id().asLongText()
        return 0; // @todo
    }

    @Override
    public void write(Object message) {
        ChannelFuture future = channel.writeAndFlush(message.toString());

        if (!future.isSuccess()) {
            throw new RuntimeException("Cannot send packet", future.cause());
        }
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public boolean isAlive() {
        return channel.channel().isActive();
    }
}
