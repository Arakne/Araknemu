package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Interface for exploration game action
 */
public interface Action {
    /**
     * Start to perform the action
     */
    public void start(ActionQueue queue);

    /**
     * Get the action performer
     */
    public ExplorationPlayer performer();

    /**
     * Get the action type
     */
    public ActionType type();

    /**
     * Get the action arguments
     */
    public Object[] arguments();
}
