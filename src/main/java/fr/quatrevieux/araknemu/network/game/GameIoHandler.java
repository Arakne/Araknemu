package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.network.in.Dispatcher;
import fr.quatrevieux.araknemu.network.in.PacketParser;
import fr.quatrevieux.araknemu.network.in.SessionClosed;
import fr.quatrevieux.araknemu.network.in.SessionCreated;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * IoHandler for Game server
 */
final public class GameIoHandler implements IoHandler {
    final private Dispatcher<GameSession> dispatcher;
    final private PacketParser parser;

    public GameIoHandler(Dispatcher<GameSession> dispatcher, PacketParser parser) {
        this.dispatcher = dispatcher;
        this.parser = parser;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {}

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        dispatcher.dispatch(
            new GameSession(session),
            new SessionCreated()
        );
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        dispatcher.dispatch(
            new GameSession(session),
            new SessionClosed()
        );
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        //@todo handle IDLE
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {}

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        dispatcher.dispatch(
            new GameSession(session),
            parser.parse(message.toString())
        );
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {}

    @Override
    public void inputClosed(IoSession session) throws Exception {}
}
