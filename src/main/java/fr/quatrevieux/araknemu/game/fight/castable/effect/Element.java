/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

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

    final static private Map<Integer, Set<Element>> bitSetCache = new HashMap<>();

    final private Characteristic boost;
    final private Characteristic fixedResistance;
    final private Characteristic percentResistance;

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

        final Set<Element> elements = EnumSet.noneOf(Element.class);

        for (Element element : values()) {
            final int id = 1 << (element.ordinal());

            if ((value & id) == id) {
                elements.add(element);
            }
        }

        bitSetCache.put(value, elements);

        return elements;
    }
}
