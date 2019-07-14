package fr.quatrevieux.araknemu.game.exploration.exchange.event;

/**
 * The acceptation state has changed
 */
final public class AcceptChanged {
    final private boolean accepted;

    public AcceptChanged(boolean accepted) {
        this.accepted = accepted;
    }

    /**
     * Get the acceptation state
     */
    public boolean accepted() {
        return accepted;
    }
}
