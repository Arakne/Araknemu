package fr.quatrevieux.araknemu.game.exploration.map.trigger;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Action perform for cell actions
 */
public interface CellActionPerformer {
    /**
     * Get the supported action
     */
    public CellAction action();

    /**
     * Perform the action on the player
     *
     * @param trigger The action trigger
     * @param player The player
     */
    public void perform(MapTrigger trigger, ExplorationPlayer player) throws Exception;
}
