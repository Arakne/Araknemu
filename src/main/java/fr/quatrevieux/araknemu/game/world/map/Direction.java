package fr.quatrevieux.araknemu.game.world.map;

import java.util.function.Function;

/**
 * List of available directions
 */
public enum Direction {
    EAST(width -> 1),
    SOUTH_EAST(width -> width),
    SOUTH(width -> 2 * width - 1),
    SOUTH_WEST(width -> width - 1),
    WEST(width -> -1),
    NORTH_WEST(width -> -width),
    NORTH(width -> -(2 * width - 1)),
    NORTH_EAST(width -> -(width - 1));

    final private Function<Integer, Integer> computeNextCell;

    Direction(Function<Integer, Integer> computeNextCell) {
        this.computeNextCell = computeNextCell;
    }

    /**
     * Get the char value of the direction
     */
    public char toChar(){
        return (char) (ordinal() + 'a');
    }

    /**
     * Get the opposite direction
     */
    public Direction opposite(){
        return Direction.values()[(ordinal() + 4) % 8];
    }

    /**
     * Get the orthogonal direction
     */
    public Direction orthogonal() {
        return Direction.values()[(ordinal() + 2) % 8];
    }

    /**
     * Does the direction is restricted
     *
     * A restricted direction can be used in fight, or by monsters's sprites
     * Restricted direction do not allow diagonal
     */
    public boolean restricted() {
        return ordinal() % 2 == 1;
    }

    /**
     * Get the increment to apply to cell id for get the next cell on the direction
     *
     * Ex:
     * MapCell current = map.get(123);
     * MapCell east = map.get(Direction.EAST.nextCellIncrement(map.dimensions().width()) + current.id());
     *
     * @param mapWidth The map width
     *
     * @return The next cell id increment
     */
    public int nextCellIncrement(int mapWidth) {
        return computeNextCell.apply(mapWidth);
    }

    /**
     * Get the direction by its char value
     */
    static public Direction byChar(char c){
        return values()[c - 'a'];
    }
}
