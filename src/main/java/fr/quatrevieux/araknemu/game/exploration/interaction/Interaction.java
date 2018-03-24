package fr.quatrevieux.araknemu.game.exploration.interaction;

/**
 * Base interface for player interactions
 */
public interface Interaction {
    /**
     * Start the interaction
     *
     * @return The started interaction or null if the interaction is aborted
     */
    public Interaction start();

    /**
     * Terminate the interaction
     */
    public void stop();
}
