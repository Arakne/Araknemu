package fr.quatrevieux.araknemu.game.exploration.action;

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
     * @todo pass the error parameter
     */
    public void stop() throws Exception;

    /**
     * End the action normally (i.e. the action is successfully done)
     */
    public void end() throws Exception;

    /**
     * Get the action id
     */
    public int id();
}
