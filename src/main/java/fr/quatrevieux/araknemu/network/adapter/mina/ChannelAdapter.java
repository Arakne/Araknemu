package fr.quatrevieux.araknemu.network.adapter.mina;

import fr.quatrevieux.araknemu.network.adapter.Channel;
import org.apache.mina.core.session.IoSession;

/**
 * Adapt apache mine IoSession to Channel
 */
final public class ChannelAdapter implements Channel {
    final private IoSession session;

    public ChannelAdapter(IoSession session) {
        this.session = session;
    }

    @Override
    public long id() {
        return session.getId();
    }

    @Override
    public void write(Object message) {
        session.write(message);
    }

    @Override
    public void close() {
        session.closeOnFlush();
    }

    @Override
    public boolean isAlive() {
        return session.isActive() && session.isConnected() && !session.isClosing();
    }
}
