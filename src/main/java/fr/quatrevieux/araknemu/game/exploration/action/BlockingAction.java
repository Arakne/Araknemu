package fr.quatrevieux.araknemu.game.exploration.action;

/**
 * Blocking action
 * This action will waiting for end or error packet for process other actions
 */
public interface BlockingAction extends Action {
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
}
