package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

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
    public Action create(ExplorationPlayer player, ActionType action, String[] arguments);
}
