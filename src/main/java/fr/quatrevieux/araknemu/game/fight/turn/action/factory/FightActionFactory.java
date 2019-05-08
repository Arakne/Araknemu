package fr.quatrevieux.araknemu.game.fight.turn.action.factory;

import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;

/**
 * Create action for fight
 */
public interface FightActionFactory {
    /**
     * Create the action
     *
     * @param arguments The action arguments
     */
    public Action create(String[] arguments);

    /**
     * Get the related action type
     */
    public ActionType type();
}
