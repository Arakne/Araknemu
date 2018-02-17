package fr.quatrevieux.araknemu.game.world.creature.accessory;

import java.util.ArrayList;
import java.util.List;

/**
 * Player accessories
 */
public interface Accessories {
    /**
     * Get an accessory by its type
     */
    public Accessory get(AccessoryType type);

    /**
     * Get all accessories
     */
    default public List<Accessory> all() {
        ArrayList<Accessory> accessories = new ArrayList<>(AccessoryType.values().length);

        for (AccessoryType type : AccessoryType.values()) {
            accessories.add(get(type));
        }

        return accessories;
    }
}
