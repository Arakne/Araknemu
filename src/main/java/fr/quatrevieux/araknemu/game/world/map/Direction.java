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
    
    final static public Direction[] RESTRICTED_DIRECTIONS = new Direction[]{NORTH_EAST, NORTH_WEST, SOUTH_WEST, SOUTH_EAST};

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
     * Get the direction by its char value
     */
    static public Direction byChar(char c){
        return values()[c - 'a'];
    }
}
