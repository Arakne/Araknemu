package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.network.AbstractSession;
import org.apache.mina.core.session.IoSession;

/**
 * Session wrapper for game server
 */
final public class GameSession extends AbstractSession {
    public GameSession(IoSession session) {
        super(session);
    }

    /**
     * Attach an game account to the session
     *
     * @param account Account to attach
     */
    public void attach(GameAccount account) {
        session.setAttribute("account", account);
    }
}
