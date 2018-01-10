package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.network.adapter.Channel;
import fr.quatrevieux.araknemu.network.adapter.SessionHandler;
import fr.quatrevieux.araknemu.network.in.Dispatcher;
import fr.quatrevieux.araknemu.network.in.PacketParser;
import fr.quatrevieux.araknemu.network.in.SessionClosed;
import fr.quatrevieux.araknemu.network.in.SessionCreated;
import fr.quatrevieux.araknemu.network.realm.in.RealmPacketParser;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * IoHandler for realm
 */
final public class RealmSessionHandler implements SessionHandler<RealmSession> {
    final private Dispatcher<RealmSession> dispatcher;
    final private PacketParser[] loginPackets;
    final private PacketParser baseParser;

    public RealmSessionHandler(Dispatcher<RealmSession> dispatcher, PacketParser[] loginPackets, PacketParser baseParser) {
        this.dispatcher = dispatcher;
        this.loginPackets = loginPackets;
        this.baseParser = baseParser;
    }

    @Override
    public void opened(RealmSession session) throws Exception {
        dispatcher.dispatch(session, new SessionCreated());
    }

    @Override
    public void closed(RealmSession session) throws Exception {
        dispatcher.dispatch(session, new SessionClosed());
    }

    @Override
    public void exception(RealmSession session, Throwable cause) {
        // If an error occurs before authenticate procedure
        // The session MUST be destroyed for security reasons
        if (!session.isLogged()) {
            session.close();
        }
    }

    @Override
    public void received(RealmSession session, String message) throws Exception {
        dispatcher.dispatch(session, session.parser().parse(message));
    }

    @Override
    public void sent(RealmSession session, Object message) throws Exception {}

    @Override
    public RealmSession create(Channel channel) {
        return new RealmSession(
            channel,
            new RealmPacketParser(
                loginPackets,
                baseParser
            )
        );
    }
}
