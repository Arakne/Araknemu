package fr.quatrevieux.araknemu.game.world.creature;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;

/**
 * Creature that can change map
 */
public interface Explorer {
    /**
     * Get the current position
     */
    public Position position();

    /**
     * Move to the cell
     *
     * @param cell The cell
     */
    public void move(ExplorationMapCell cell);

    /**
     * Get the current map
     */
    public ExplorationMap map();
}
