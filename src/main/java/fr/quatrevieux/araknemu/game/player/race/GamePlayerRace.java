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

package fr.quatrevieux.araknemu.game.player.race;

import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.BoostStatsData;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.game.spell.SpellLevels;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Player race data
 */
final public class GamePlayerRace {
    final private PlayerRace entity;
    final private Map<Integer, SpellLevels> spells;

    public GamePlayerRace(PlayerRace entity, List<SpellLevels> spells) {
        this.entity = entity;
        this.spells = spells.stream()
            .collect(
                Collectors.toMap(
                    SpellLevels::id,
                    Function.identity(),
                    (o, o2) -> {
                        throw new IllegalArgumentException("Duplicate");
                    },
                    LinkedHashMap::new
                )
            )
        ;
    }

    /**
     * Get the race life per level
     */
    public int life(int level) {
        return entity.startLife() + (level - 1) * entity.perLevelLife();
    }

    /**
     * Get the race id
     */
    public Race race() {
        return entity.race();
    }

    /**
     * Get the race name
     */
    public String name() {
        return entity.name();
    }

    /**
     * Get the race start stats
     */
    public Characteristics baseStats(int level) {
        for (Map.Entry<Integer, Characteristics> entry  : entity.baseStats().entrySet()) {
            if (entry.getKey() <= level) {
                return entry.getValue();
            }
        }

        throw new IllegalArgumentException("Not handled level " + level + " for race stats " + name());
    }

    /**
     * Get start discernment
     */
    public int startDiscernment() {
        return entity.startDiscernment();
    }

    /**
     * Get start pods
     */
    public int startPods() {
        return entity.startPods();
    }

    /**
     * Get the initiative value for current life
     */
    public int initiative(int life) {
        return life / (boost(Characteristic.VITALITY, 0).boost() * 4);
    }

    /**
     * Get the boost stats interval for the current characteristic and value
     *
     * @param characteristic Characteristic to check
     * @param currentValue The current player base characteristic value
     */
    public BoostStatsData.Interval boost(Characteristic characteristic, int currentValue) {
        return entity.boostStats().get(characteristic, currentValue);
    }

    public Collection<SpellLevels> spells() {
        return spells.values();
    }

    /**
     * The Astrub statue position
     */
    public Position astrubPosition() {
        return entity.astrubPosition();
    }
}
