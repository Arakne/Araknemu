package fr.quatrevieux.araknemu.game.exploration.map.cell;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.creature.Operation;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

/**
 * Cell of exploration map
 */
public interface ExplorationMapCell extends MapCell {
    @Override
    public ExplorationMap map();

    /**
     * Does the cell is free ?
     * A cell is free is a new sprite can be added on, and no other objects is present
     */
    public boolean free();

    /**
     * Apply an operation to all creatures on current cell
     *
     * @see Creature#apply(Operation)
     */
    default public void apply(Operation operation)  {
        // Optimisation : the cell is not walkable, no creatures can be located here
        if (!walkable()) {
            return;
        }

        for (Creature creature : map().creatures()) {
            if (equals(creature.cell())) {
                creature.apply(operation);
            }
        }
    }
}
