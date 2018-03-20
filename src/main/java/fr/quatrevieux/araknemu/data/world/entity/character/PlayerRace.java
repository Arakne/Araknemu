package fr.quatrevieux.araknemu.data.world.entity.character;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.value.BoostStatsData;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

import java.util.SortedMap;

/**
 * Entity for player race
 */
final public class PlayerRace {
    final private Race race;
    final private String name;
    final private SortedMap<Integer, Characteristics> baseStats;
    final private int startDiscernment;
    final private int startPods;
    final private int startLife;
    final private int perLevelLife;
    final private BoostStatsData boostStats;
    final private Position startPosition;
    final private int[] spells;

    public PlayerRace(Race race, String name, SortedMap<Integer, Characteristics> baseStats, int startDiscernment, int startPods, int startLife, int perLevelLife, BoostStatsData boostStats, Position startPosition, int[] spells) {
        this.race = race;
        this.name = name;
        this.baseStats = baseStats;
        this.startDiscernment = startDiscernment;
        this.startPods = startPods;
        this.startLife = startLife;
        this.perLevelLife = perLevelLife;
        this.boostStats = boostStats;
        this.startPosition = startPosition;
        this.spells = spells;
    }

    public PlayerRace(Race race) {
        this(race, null, null, 0, 0, 0, 0, null, null, null);
    }

    public Race race() {
        return race;
    }

    public String name() {
        return name;
    }

    public SortedMap<Integer, Characteristics> baseStats() {
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

    public int[] spells() {
        return spells;
    }
}
