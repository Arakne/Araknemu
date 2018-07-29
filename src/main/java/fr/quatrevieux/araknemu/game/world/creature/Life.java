package fr.quatrevieux.araknemu.game.world.creature;

/**
 * Store life points of a creature
 */
public interface Life {
    /**
     * Get the maximum creature life
     */
    public int max();

    /**
     * Get the current creature life point
     */
    public int current();

    /**
     * Check if the creature is full life
     */
    default public boolean isFull() {
        return max() == current();
    }
}
