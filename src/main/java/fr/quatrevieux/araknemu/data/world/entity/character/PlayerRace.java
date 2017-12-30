package fr.quatrevieux.araknemu.data.world.entity.character;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Entity for player race
 */
final public class PlayerRace {
    final private Race race;
    final private String name;
    final private Characteristics baseStats;

    public PlayerRace(Race race, String name, Characteristics baseStats) {
        this.race = race;
        this.name = name;
        this.baseStats = baseStats;
    }

    public Race race() {
        return race;
    }

    public String name() {
        return name;
    }

    public Characteristics baseStats() {
        return baseStats;
    }
}
