package fr.quatrevieux.araknemu.game.world.map;

/**
 * Map cell
 */
public interface MapCell {
    /**
     * Get the cell id
     */
    public int id();

    /**
     * Check if the cell is walkable
     */
    public boolean walkable();

    /**
     * Check if two map cells are equals
     *
     * @param other cell to check
     */
    default boolean equals(MapCell other) {
        if (other == null) {
            return false;
        }

        return other.id() == id();
    }
}
