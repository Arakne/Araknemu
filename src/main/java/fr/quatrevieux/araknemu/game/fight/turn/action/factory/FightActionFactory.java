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
     * @param action The action type
     * @param arguments The action arguments
     */
    public Action create(ActionType action, String[] arguments);
}
