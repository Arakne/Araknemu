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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.world.entity.character;

import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.data.value.BoostStatsData;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

import java.util.SortedMap;

/**
 * Entity for player race
 */
public final class PlayerRace {
    private final Race race;
    private final String name;
    private final SortedMap<Integer, Characteristics> baseStats;
    private final int startDiscernment;
    private final int startPods;
    private final int startLife;
    private final int perLevelLife;
    private final BoostStatsData boostStats;
    private final Position startPosition;
    private final Position astrubPosition;
    private final int[] spells;

    public PlayerRace(Race race, String name, SortedMap<Integer, Characteristics> baseStats, int startDiscernment, int startPods, int startLife, int perLevelLife, BoostStatsData boostStats, Position startPosition, Position astrubPosition, int[] spells) {
        this.race = race;
        this.name = name;
        this.baseStats = baseStats;
        this.startDiscernment = startDiscernment;
        this.startPods = startPods;
        this.startLife = startLife;
        this.perLevelLife = perLevelLife;
        this.boostStats = boostStats;
        this.startPosition = startPosition;
        this.astrubPosition = astrubPosition;
        this.spells = spells;
    }

    public PlayerRace(Race race) {
        this(race, null, null, 0, 0, 0, 0, null, null, null, null);
    }

    public Race race() {
        return race;
    }

    /**
     * Get the race name (not used on process : for human)
     */
    public String name() {
        return name;
    }

    /**
     * Get the base stats of the race
     * This include AP, MP
     * The stats are indexed by the minimum level for applying (ex: 7 AP on level 100)
     */
    public SortedMap<Integer, Characteristics> baseStats() {
        return baseStats;
    }

    /**
     * The base discernment for the race
     */
    public int startDiscernment() {
        return startDiscernment;
    }

    /**
     * The base pods for the race
     */
    public int startPods() {
        return startPods;
    }

    /**
     * The base life for the race
     */
    public int startLife() {
        return startLife;
    }

    /**
     * Number of life point win per level
     */
    public int perLevelLife() {
        return perLevelLife;
    }

    /**
     * Boost stats rules
     */
    public BoostStatsData boostStats() {
        return boostStats;
    }

    /**
     * The start position (incarman statue)
     */
    public Position startPosition() {
        return startPosition;
    }

    /**
     * The astrub statue position
     */
    public Position astrubPosition() {
        return astrubPosition;
    }

    /**
     * List of race spells ids
     *
     * @see fr.quatrevieux.araknemu.data.world.entity.SpellTemplate#id()
     */
    public int[] spells() {
        return spells;
    }
}
