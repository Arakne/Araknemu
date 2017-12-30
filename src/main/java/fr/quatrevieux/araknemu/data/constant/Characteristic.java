package fr.quatrevieux.araknemu.data.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * List of characteristics
 */
public enum Characteristic {
    INITIATIVE,
    DISCERNMENT,
    ACTION_POINT,
    MOVEMENT_POINT,
    STRENGTH,
    VITALITY,
    WISDOM,
    LUCK,
    AGILITY,
    INTELLIGENCE,
    SIGHT_BOOST,
    MAX_SUMMONED_CREATURES,
    FIXED_DAMAGE,
    PHYSICAL_DAMAGE,
    WEAPON_MASTER,
    PERCENT_DAMAGE,
    HEALTH_BOOST,
    TRAP_BOOST,
    PERCENT_TRAP_BOOST,
    COUNTER_DAMAGE,
    CRITICAL_BONUS,
    FAIL_MALUS,
    RESISTANCE_ACTION_POINT,
    RESISTANCE_MOVEMENT_POINT,
    RESISTANCE_NEUTRAL,
    PERCENT_RESISTANCE_NEUTRAL,
    RESISTANCE_PVP_NEUTRAL,
    RESISTANCE_PERCENT_PVP_NEUTRAL,
    RESISTANCE_EARTH,
    RESISTANCE_PERCENT_EARTH,
    RESISTANCE_PVP_EARTH,
    RESISTANCE_PERCENT_PVP_EARTH,
    RESISTANCE_WATTER,
    RESISTANCE_PERCENT_WATTER,
    RESISTANCE_PVP_WATTER,
    RESISTANCE_PERCENT_PVP_WATTER,
    RESISTANCE_AIR,
    RESISTANCE_PERCENT_AIR,
    RESISTANCE_PVP_AIR,
    RESISTANCE_PERCENT_PVP_AIR,
    RESISTANCE_FIRE,
    RESISTANCE_PERCENT_FIRE,
    RESISTANCE_PVP_FIRE,
    RESISTANCE_PERCENT_PVP_FIRE;

    final static private Map<Integer, Characteristic> characteristicsById = new HashMap<>();

    static  {
        for (Characteristic characteristic : values()) {
            characteristicsById.put(characteristic.id(), characteristic);
        }
    }

    /**
     * Get the characteristic race
     */
    public int id() {
        return ordinal() + 8;
    }

    /**
     * Get characteristic by its race
     */
    static public Characteristic fromId(int id) {
        return characteristicsById.get(id);
    }
}
