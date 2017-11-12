package fr.quatrevieux.araknemu.network.realm;

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
 *
 * @todo remove RealmService
 */
final public class RealmIoHandler implements IoHandler {
    final private Dispatcher<RealmSession> dispatcher;
    final private PacketParser[] loginPackets;
    final private PacketParser baseParser;

    public RealmIoHandler(Dispatcher<RealmSession> dispatcher, PacketParser[] loginPackets, PacketParser baseParser) {
        this.dispatcher = dispatcher;
        this.loginPackets = loginPackets;
        this.baseParser = baseParser;
    }

    public void sessionCreated(IoSession session) throws Exception {}

    public void sessionOpened(IoSession session) throws Exception {
        dispatcher.dispatch(
            new RealmSession(session),
            new SessionCreated()
        );
    }

    public void sessionClosed(IoSession session) throws Exception {
        dispatcher.dispatch(
            new RealmSession(session),
            new SessionClosed()
        );
    }

    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

    }

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        RealmSession realmSession = new RealmSession(session);

        // If an error occurs before authenticate procedure
        // The session MUST be destroyed for security reasons
        if (!realmSession.isLogged()) {
            realmSession.close();
        }
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
        RealmSession realmSession = new RealmSession(session);

        dispatcher.dispatch(
            realmSession,
            parser(realmSession).parse(message.toString())
        );
    }

    public void messageSent(IoSession session, Object message) throws Exception {

    }

    public void inputClosed(IoSession session) throws Exception {

    }

    /**
     * Get the parser from session
     * @param session
     * @return
     */
    private PacketParser parser(RealmSession session) {
        if (session.hasParser()) {
            return session.parser();
        }

        PacketParser parser = new RealmPacketParser(
            loginPackets,
            baseParser
        );

        session.setParser(parser);

        return parser;
    }
}
