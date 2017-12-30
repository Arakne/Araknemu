package fr.quatrevieux.araknemu.game.world.creature.characteristics;

import fr.quatrevieux.araknemu.data.constant.Characteristic;

import java.util.EnumMap;
import java.util.Map;

/**
 * Simple implementation for characteristics map
 */
final public class DefaultCharacteristics implements MutableCharacteristics {
    final private Map<Characteristic, Integer> map = new EnumMap<>(Characteristic.class);

    @Override
    public int get(Characteristic characteristic) {
        return map.getOrDefault(characteristic, 0);
    }

    @Override
    public void set(Characteristic characteristic, int value) {
        map.put(characteristic, value);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Characteristics) {
            return equals((Characteristics) obj);
        }

        return false;
    }

    /**
     * Check equality from two abstract characteristics map
     *
     * Two characteristics map are equals if and only if all characteristic values are equals
     */
    public boolean equals(Characteristics other) {
        for (Characteristic characteristic : Characteristic.values()) {
            if (get(characteristic) != other.get(characteristic)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int h = 0;

        for (Characteristic characteristic : Characteristic.values()) {
            int value = get(characteristic);

            if (value != 0) {
                h += characteristic.hashCode() ^ value;
            }
        }

        return h;
    }
}
