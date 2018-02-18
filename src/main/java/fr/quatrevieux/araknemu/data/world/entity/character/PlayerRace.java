package fr.quatrevieux.araknemu.data.world.entity.character;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.value.BoostStatsData;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Entity for player race
 */
final public class PlayerRace {
    final private Race race;
    final private String name;
    final private Characteristics baseStats;
    final private int startDiscernment;
    final private int startPods;
    final private int startLife;
    final private int perLevelLife;
    final private BoostStatsData boostStats;
    final private Position startPosition;

    public PlayerRace(Race race, String name, Characteristics baseStats, int startDiscernment, int startPods, int startLife, int perLevelLife, BoostStatsData boostStats, Position startPosition) {
        this.race = race;
        this.name = name;
        this.baseStats = baseStats;
        this.startDiscernment = startDiscernment;
        this.startPods = startPods;
        this.startLife = startLife;
        this.perLevelLife = perLevelLife;
        this.boostStats = boostStats;
        this.startPosition = startPosition;
    }

    public PlayerRace(Race race) {
        this(race, null, null, 0, 0, 0, 0, null, null);
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

    public int startDiscernment() {
        return startDiscernment;
    }

    public int startPods() {
        return startPods;
    }

    public int startLife() {
        return startLife;
    }

    public int perLevelLife() {
        return perLevelLife;
    }

    public BoostStatsData boostStats() {
        return boostStats;
    }

    public Position startPosition() {
        return startPosition;
    }
}
