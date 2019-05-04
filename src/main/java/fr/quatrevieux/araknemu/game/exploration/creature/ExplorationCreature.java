package fr.quatrevieux.araknemu.game.exploration.creature;

import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * Base type for creature in exploration mode
 */
public interface ExplorationCreature extends Creature<ExplorationMapCell> {

    /**
     * Apply the operation on the creature
     */
    public void apply(Operation operation);
}
