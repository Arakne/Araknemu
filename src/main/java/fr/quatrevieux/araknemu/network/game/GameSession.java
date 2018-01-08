package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
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

    /**
     * Get the attached account
     */
    public GameAccount account() {
        return (GameAccount) session.getAttribute("account");
    }

    /**
     * Check if an account is attached
     */
    public boolean isLogged() {
        return session.containsAttribute("account");
    }

    /**
     * Remove the attached account
     * @return The attached account
     */
    public GameAccount detach() {
        return (GameAccount) session.removeAttribute("account");
    }

    /**
     * Set the logged player
     */
    public void setPlayer(GamePlayer player) {
        session.setAttribute("player", player);
    }

    /**
     * Get the logged player
     *
     * @return The player instance, or null is not in game
     */
    public GamePlayer player() {
        return (GamePlayer) session.getAttribute("player");
    }

    /**
     * Get the exploration player
     *
     * @return The player instance, or null if not on exploration
     */
    public ExplorationPlayer exploration() {
        return (ExplorationPlayer) session.getAttribute("exploration");
    }

    /**
     * Set the exploration player
     */
    public void setExploration(ExplorationPlayer exploration) {
        session.setAttribute("exploration", exploration);
    }
}
