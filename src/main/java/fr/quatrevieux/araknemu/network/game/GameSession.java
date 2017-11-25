package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.network.AbstractSession;
import org.apache.mina.core.session.IoSession;

/**
 * Session wrapper for game server
 */
final public class GameSession extends AbstractSession {
    public GameSession(IoSession session) {
        super(session);
    }
}
