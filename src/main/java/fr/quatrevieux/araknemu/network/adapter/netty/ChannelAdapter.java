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
}
