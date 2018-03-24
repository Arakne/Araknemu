package fr.quatrevieux.araknemu.game.exploration.interaction;

/**
 * Interface for cancellable / declinable interactions
 */
public interface Declinable {
    /**
     * Decline the invitation and stop the interaction without perform any actions
     */
    public void decline();
}
