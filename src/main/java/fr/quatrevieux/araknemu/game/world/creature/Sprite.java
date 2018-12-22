package fr.quatrevieux.araknemu.game.world.creature;

import fr.quatrevieux.araknemu.game.world.map.Direction;

/**
 * Interface for representing sprites
 *
 * A sprite is a displayable creature
 */
public interface Sprite {
    public enum Type {
        PLAYER,
        CREATURE, //?
        MONSTER,
        MONSTER_GROUP,
        NPC,
        OFFLINE_CHARACTER, //seller ?
        COLLECTOR,
        MUTANT,
        MUTANT_PLAYER,
        MOUNT_PARK,
        PRISM;

        /**
         * Get the sprite type id
         * The id is a negative integer
         */
        public int id() {
            return -ordinal();
        }
    }

    /**
     * Get the sprite id
     * This id MUST be unique over all the map
     */
    public int id();

    /**
     * Get the map cell where the sprite is located
     */
    public int cell();

    /**
     * Get the sprite orientation
     * Some sprites can only are restricted to 4 directions
     * The playable sprites supports all the 8 directions
     */
    public Direction orientation();

    /**
     * Get the sprite type
     */
    public Type type();

    /**
     * Get the sprite name
     */
    public String name();

    /**
     * Render the sprite string
     */
    public String toString();
}
