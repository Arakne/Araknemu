package fr.quatrevieux.araknemu.game.world.creature.accessory;

/**
 * Interface for sprite accessory
 */
public interface Accessory {
    /**
     * Get the accessory type
     */
    public AccessoryType type();

    /**
     * Get the accessory appearance
     */
    public int appearance();

    /**
     * Get the accessory item type
     * This method return -1 to use the appearance type
     */
    default public int itemType() {
        return -1;
    }

    /**
     * Get the accessory display frame
     * This method must return 0 if not custom item type is given
     */
    default public int frame() {
        return 0;
    }
}
