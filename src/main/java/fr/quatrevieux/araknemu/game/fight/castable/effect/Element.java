package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.data.constant.Characteristic;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Effects elements
 */
public enum Element {
    NEUTRAL(Characteristic.STRENGTH,  Characteristic.RESISTANCE_NEUTRAL, Characteristic.RESISTANCE_PERCENT_NEUTRAL),
    EARTH(Characteristic.STRENGTH,    Characteristic.RESISTANCE_EARTH,   Characteristic.RESISTANCE_PERCENT_EARTH),
    WATER(Characteristic.LUCK,        Characteristic.RESISTANCE_WATER,   Characteristic.RESISTANCE_PERCENT_WATER),
    AIR(Characteristic.AGILITY,       Characteristic.RESISTANCE_AIR,     Characteristic.RESISTANCE_PERCENT_AIR),
    FIRE(Characteristic.INTELLIGENCE, Characteristic.RESISTANCE_FIRE,    Characteristic.RESISTANCE_PERCENT_FIRE);

    final private Characteristic boost;
    final private Characteristic fixedResistance;
    final private Characteristic percentResistance;

    static private Map<Integer, Set<Element>> bitSetCache = new HashMap<>();

    Element(Characteristic boost, Characteristic fixedResistance, Characteristic percentResistance) {
        this.boost = boost;
        this.fixedResistance = fixedResistance;
        this.percentResistance = percentResistance;
    }

    public Characteristic boost() {
        return boost;
    }

    public Characteristic fixedResistance() {
        return fixedResistance;
    }

    public Characteristic percentResistance() {
        return percentResistance;
    }

    /**
     * Extract set of elements from a bit set
     *
     * @param value The bit set
     */
    static public Set<Element> fromBitSet(int value) {
        if (bitSetCache.containsKey(value)) {
            return bitSetCache.get(value);
        }

        Set<Element> elements = EnumSet.noneOf(Element.class);

        for (Element element : values()) {
            int id = 1 << (element.ordinal());

            if ((value & id) == id) {
                elements.add(element);
            }
        }

        bitSetCache.put(value, elements);

        return elements;
    }
}
