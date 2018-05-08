package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.data.constant.Characteristic;

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
}
