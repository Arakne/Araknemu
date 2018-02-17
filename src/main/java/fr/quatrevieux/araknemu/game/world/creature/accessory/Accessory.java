package fr.quatrevieux.araknemu.game.world.creature.accessory;

import fr.quatrevieux.araknemu.game.world.item.Type;

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
     * This method return null to use the appearance type
     */
    default public Type itemType() {
        return null;
    }

    /**
     * Get the accessory display frame
     * This method must return 0 if not custom item type is given
     */
    default public int frame() {
        return 0;
    }
}
