package fr.quatrevieux.araknemu.game.world.map;

/**
 * List of available directions
 */
public enum Direction {
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST,
    NORTH,
    NORTH_EAST;

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
     * Does the direction is restricted
     *
     * A restricted direction can be used in fight, or by monsters's sprites
     * Restricted direction do not allow diagonal
     */
    public boolean restricted() {
        return ordinal() % 2 == 1;
    }

    /**
     * Get the direction by its char value
     */
    static public Direction byChar(char c){
        return values()[c - 'a'];
    }
}
