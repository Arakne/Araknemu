package fr.quatrevieux.araknemu.game.exploration.interaction.action.factory;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
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
     * @param request The action request
     *
     * @return The new action
     */
    public Action create(ExplorationPlayer player, GameActionRequest request) throws Exception;
}
