package fr.quatrevieux.araknemu.game.world.creature;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

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
     * @param cell The cell id
     */
    public void move(int cell);

    /**
     * Get the current map
     */
    public ExplorationMap map();
}
