package fr.quatrevieux.araknemu.game.world.creature.accessory;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * List type of accessories
 */
public enum AccessoryType {
    WEAPON(1),
    HELMET(6),
    MANTLE(7),
    PET(8),
    SHIELD(15);

    final private int slot;

    final static private Map<Integer, AccessoryType> typesBySlot = Arrays
        .stream(values())
        .collect(Collectors.toMap(AccessoryType::slot, Function.identity()))
    ;

    final static private int[] SLOT_IDS = Arrays.stream(values()).mapToInt(AccessoryType::slot).toArray();

    AccessoryType(int slot) {
        this.slot = slot;
    }

    /**
     * Get the related inventory slot
     */
    public int slot() {
        return slot;
    }

    /**
     * Get the type by slot id
     *
     * @return The accessory type or null if no accessory is related to the slot
     */
    static public AccessoryType bySlot(int slot) {
        return typesBySlot.get(slot);
    }

    /**
     * Get accessories slot ids
     */
    static public int[] slots() {
        return SLOT_IDS;
    }

    /**
     * Check if the slot is an accessory
     */
    static public boolean isAccessorySlot(int slotId) {
        return typesBySlot.containsKey(slotId);
    }
}
