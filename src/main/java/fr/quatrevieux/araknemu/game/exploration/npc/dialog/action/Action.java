package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Action to perform after a response
 */
public interface Action {
    /**
     * Check if the action can be performed to the exploration player
     * If at least on action cannot be performed for a response, the response will not be available
     */
    public boolean check(ExplorationPlayer player);

    /**
     * Apply the action to the player
     * The player must be check()'d before
     */
    public void apply(ExplorationPlayer player);
}
