package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.network.adapter.Channel;
import fr.quatrevieux.araknemu.network.adapter.SessionHandler;
import fr.quatrevieux.araknemu.network.exception.CloseSession;
import fr.quatrevieux.araknemu.network.exception.WritePacket;
import fr.quatrevieux.araknemu.network.in.Dispatcher;
import fr.quatrevieux.araknemu.network.in.PacketParser;
import fr.quatrevieux.araknemu.network.in.SessionClosed;
import fr.quatrevieux.araknemu.network.in.SessionCreated;

/**
 * IoHandler for Game server
 */
final public class GameSessionHandler implements SessionHandler<GameSession> {
    final private Dispatcher<GameSession> dispatcher;
    final private PacketParser parser;

    public GameSessionHandler(Dispatcher<GameSession> dispatcher, PacketParser parser) {
        this.dispatcher = dispatcher;
        this.parser = parser;
    }

    @Override
    public void opened(GameSession session) throws Exception {
        dispatcher.dispatch(session, new SessionCreated());
    }

    @Override
    public void closed(GameSession session) throws Exception {
        dispatcher.dispatch(session, new SessionClosed());
    }

    @Override
    public void exception(GameSession session, Throwable cause) {
        if (cause instanceof WritePacket) {
            session.write(
                ((WritePacket) cause).packet()
            );
        }

        if (cause instanceof CloseSession) {
            session.close();
        }
    }

    @Override
    public void received(GameSession session, String message) throws Exception {
        dispatcher.dispatch(session, parser.parse(message));
    }

    @Override
    public void sent(GameSession session, Object message) throws Exception {}

    @Override
    public GameSession create(Channel channel) {
        return new GameSession(channel);
    }
}
