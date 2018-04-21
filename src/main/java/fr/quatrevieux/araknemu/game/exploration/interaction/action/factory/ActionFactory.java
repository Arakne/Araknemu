package fr.quatrevieux.araknemu.game.exploration.interaction.action.factory;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;

/**
 * Create an action for an exploration player
 */
public interface ActionFactory {
    /**
     * Create the requested action
     *
     * @param player The player which perform the action
     * @param action The action type
     * @param arguments The action arguments
     *
     * @return The new action
     */
    public Action create(ExplorationPlayer player, ActionType action, String[] arguments) throws Exception;
}
