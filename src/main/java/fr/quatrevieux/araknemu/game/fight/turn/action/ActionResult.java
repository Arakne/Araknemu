package fr.quatrevieux.araknemu.game.fight.turn.action;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Results of a fight action
 */
public interface ActionResult {
    /**
     * The fight action type
     */
    public int action();

    /**
     * The action performer
     */
    public Fighter performer();

    /**
     * The action arguments
     */
    public Object[] arguments();

    /**
     * Does the action is successful ?
     *
     * A failed action will not be keep on the action handler
     */
    public boolean success();
}
