package fr.quatrevieux.araknemu.game.exploration.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Interface for game action
 *
 * @todo refactor with fight
 */
public interface Action {
    /**
     * Start to perform the action
     */
    public void start() throws Exception;

    /**
     * The action must be stopped Unexpectedly
     *
     * @param argument The cancel argument
     */
    public void cancel(String argument) throws Exception;

    /**
     * End the action normally (i.e. the action is successfully done)
     */
    public void end() throws Exception;

    /**
     * Get the action id
     */
    public int id();

    /**
     * Set the action ID
     *
     * This method is internal
     */
    public void setId(int id);

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
