package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellAction;

/**
 * Cell which trigger an action
 */
final public class TriggerCell implements ExplorationMapCell {
    final private int id;
    final private CellAction action;
    final private ExplorationMap map;

    public TriggerCell(int id, CellAction action, ExplorationMap map) {
        this.id = id;
        this.action = action;
        this.map = map;
    }

    @Override
    public ExplorationMap map() {
        return map;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public boolean walkable() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TriggerCell && equals((TriggerCell) obj);
    }

    /**
     * Perform action when a player stops on the cell after a move
     *
     * @param player The player
     */
    public void onStop(ExplorationPlayer player) {
        action.perform(player);
    }
}
