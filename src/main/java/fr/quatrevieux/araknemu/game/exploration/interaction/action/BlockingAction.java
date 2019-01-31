package fr.quatrevieux.araknemu.game.exploration.interaction.action;

/**
 * Blocking action
 * This action will waiting for end or error packet for process other actions
 */
public interface BlockingAction extends Action {
    /**
     * The action must be stopped Unexpectedly
     * Note: This method should not raise any exception : it used for {@link ActionQueue#stop()}
     * After this call, the action is considered as done, and will be destroyed.
     * So it must let the exploration into a valid state
     *
     * @param argument The cancel argument. Null if called by {@link ActionQueue#stop()}
     */
    public void cancel(String argument);

    /**
     * End the action normally (i.e. the action is successfully done)
     */
    public void end();

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
