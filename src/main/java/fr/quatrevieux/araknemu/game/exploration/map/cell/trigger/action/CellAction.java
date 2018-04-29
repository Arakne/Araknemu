package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;

/**
 * Action for trigger cell
 */
public interface CellAction {
    /**
     * Perform the action on the player
     *
     * @param player The player
     */
    public void perform(ExplorationPlayer player);

    /**
     * Get the trigger cell id
     */
    public int cell();
}
