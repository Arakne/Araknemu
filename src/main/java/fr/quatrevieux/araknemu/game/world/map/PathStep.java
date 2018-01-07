package fr.quatrevieux.araknemu.game.world.map;

/**
 * Step for a path
 */
final public class PathStep {
    final private int cell;
    final private Direction direction;

    public PathStep(int cell, Direction direction) {
        this.cell = cell;
        this.direction = direction;
    }

    public int cell() {
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
