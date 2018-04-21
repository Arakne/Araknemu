package fr.quatrevieux.araknemu.game.world.map.path;

import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

/**
 * Step for a path
 */
final public class PathStep<C extends MapCell> {
    final private C cell;
    final private Direction direction;

    public PathStep(C cell, Direction direction) {
        this.cell = cell;
        this.direction = direction;
    }

    public C cell() {
        return cell;
    }

    public Direction direction() {
        return direction;
    }

    @Override
    public String toString() {
        return "{" + cell + ", " + direction + "}";
    }
}
