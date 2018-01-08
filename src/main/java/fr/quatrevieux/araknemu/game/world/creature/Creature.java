package fr.quatrevieux.araknemu.game.world.creature;

/**
 * Base interface for Dofus creatures (monsters, players...)
 */
public interface Creature {
    /**
     * Get the sprite
     */
    public Sprite sprite();

    /**
     * Get the creature id
     */
    public int id();

    /**
     * Get the creature cell id
     */
    public int cell();
}
