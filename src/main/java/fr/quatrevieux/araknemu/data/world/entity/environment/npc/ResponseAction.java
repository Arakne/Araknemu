package fr.quatrevieux.araknemu.data.world.entity.environment.npc;

/**
 * Action to perform for a response
 */
final public class ResponseAction {
    final private int responseId;
    final private String action;
    final private String arguments;

    public ResponseAction(int responseId, String action, String arguments) {
        this.responseId = responseId;
        this.action = action;
        this.arguments = arguments;
    }

    /**
     * Get the response id
     * The id is located into swf dialog_xx.swf into D.a[id]
     * This is not the primary key (i.e. not unique), but a part with action
     */
    public int responseId() {
        return responseId;
    }

    /**
     * Get the action name to perform with the response
     * This action is part of the primary key (with responseId)
     * This means that the same action cannot be performed more than once for a response
     */
    public String action() {
        return action;
    }

    /**
     * Get the action arguments
     * The format depends of the action
     */
    public String arguments() {
        return arguments;
    }
}
