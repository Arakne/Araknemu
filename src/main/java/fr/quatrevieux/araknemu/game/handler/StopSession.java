package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import fr.quatrevieux.araknemu.network.in.SessionClosed;

/**
 * Handle end of session
 */
final public class StopSession implements PacketHandler<GameSession, SessionClosed> {
    @Override
    public void handle(GameSession session, SessionClosed packet) {
        // @todo 
    }

    @Override
    public Class<SessionClosed> packet() {
        return SessionClosed.class;
    }
}
