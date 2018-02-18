package fr.quatrevieux.araknemu.game.player.characteristic;

import java.util.EnumMap;
import java.util.Map;

/**
 * Handle special effects
 */
final public class SpecialEffects {
    public enum Type {
        PODS,
        INITIATIVE,
        DISCERNMENT
    }

    final private Map<Type, Integer> map = new EnumMap<>(Type.class);

    /**
     * Add effect value to the map
     */
    public void add(Type type, int value) {
        map.put(type, map.getOrDefault(type, 0) + value);
    }

    /**
     * Subtract effect value to the map
     */
    public void sub(Type type, int value) {
        map.put(type, map.getOrDefault(type, 0) - value);
    }

    /**
     * Get the special effect value
     */
    public int get(Type type) {
        return map.getOrDefault(type, 0);
    }

    /**
     * Clear the special effects map
     */
    public void clear() {
        map.clear();
    }
}
