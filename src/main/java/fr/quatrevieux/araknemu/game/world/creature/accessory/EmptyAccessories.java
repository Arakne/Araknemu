package fr.quatrevieux.araknemu.game.world.creature.accessory;

import java.util.Collections;
import java.util.List;

/**
 * Empty accessories
 */
final public class EmptyAccessories implements Accessories {
    @Override
    public Accessory get(AccessoryType type) {
        return new NullAccessory(type);
    }

    @Override
    public List<Accessory> all() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "";
    }
}
