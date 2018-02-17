package fr.quatrevieux.araknemu.game.world.creature.accessory;

/**
 * Null object for accessory
 */
final public class NullAccessory implements Accessory {
    final private AccessoryType type;

    public NullAccessory(AccessoryType type) {
        this.type = type;
    }

    @Override
    public AccessoryType type() {
        return type;
    }

    @Override
    public int appearance() {
        return 0;
    }

    @Override
    public String toString() {
        return "";
    }
}
