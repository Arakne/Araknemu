package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.game.event.common.Disconnected;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.in.SessionClosed;

/**
 * Handle end of session
 */
final public class StopSession implements PacketHandler<GameSession, SessionClosed> {
    @Override
    public void handle(GameSession session, SessionClosed packet) {
        if (session.exploration() != null) {
            session.exploration().dispatch(new Disconnected());
            session.setExploration(null);
        }

        if (session.player() != null) {
            session.player().dispatch(new Disconnected());
            session.setPlayer(null);
        }

        if (session.isLogged()) {
            session.account().detach();
        }
    }

    @Override
    public Class<SessionClosed> packet() {
        return SessionClosed.class;
    }
}
