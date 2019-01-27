package fr.quatrevieux.araknemu.game.exploration.npc.dialog;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;

import java.util.List;

/**
 * Dialog response
 * The response will perform actions on the interlocutor
 */
final public class Response {
    final private int id;
    final private List<Action> actions;

    public Response(int id, List<Action> actions) {
        this.id = id;
        this.actions = actions;
    }

    /**
     * Get the response id
     * The id is located into swf dialog_xx.swf into D.a[id]
     *
     * @see ResponseAction#responseId()
     */
    public int id() {
        return id;
    }

    /**
     * Check if the response is valid
     *
     * A response is valid, if and only if all its actions can be performed
     * A response without actions cannot be considered as valid
     */
    public boolean check(ExplorationPlayer player) {
        return !actions.isEmpty() && actions.stream().allMatch(action -> action.check(player));
    }

    /**
     * Apply all actions to the player
     */
    public void apply(ExplorationPlayer player) {
        for (Action action : actions) {
            action.apply(player);
        }
    }
}
