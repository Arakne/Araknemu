package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.in.SessionClosed;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Handle end of session
 */
final public class StopSession implements PacketHandler<GameSession, SessionClosed> {
    @Override
    public void handle(GameSession session, SessionClosed packet) {
        Stream.of(session.exploration(), session.fighter(), session.player())
            .filter(Objects::nonNull)
            .forEach(scope -> {
                scope.dispatch(new Disconnected());
                scope.unregister(session);
            })
        ;

        if (session.isLogged()) {
            session.account().detach();
        }
    }

    @Override
    public Class<SessionClosed> packet() {
        return SessionClosed.class;
    }
}
