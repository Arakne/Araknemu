package fr.quatrevieux.araknemu.game.exploration.map.event;

import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.map.path.Path;

/**
 * A creature is moving on the map
 */
final public class CreatureMoving {
    final private Creature<ExplorationMapCell> creature;
    final private Path<ExplorationMapCell> path;

    public CreatureMoving(Creature<ExplorationMapCell> creature, Path<ExplorationMapCell> path) {
        this.creature = creature;
        this.path = path;
    }

    /**
     * Get the moving creature
     */
    public Creature<ExplorationMapCell> creature() {
        return creature;
    }

    /**
     * Get the move path
     */
    public Path<ExplorationMapCell> path() {
        return path;
    }
}
