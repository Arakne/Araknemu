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

package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.PvmBuilder;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handle a monster group configuration on map and cell
 */
public final class LivingMonsterGroupPosition {
    private final MonsterGroupFactory factory;
    private final FightService fightService;
    private final MonsterEnvironmentService environmentService;
    private final boolean fixed;

    private final MonsterGroupData data;
    private final SpawnCellSelector cellSelector;

    private ExplorationMap map;

    public LivingMonsterGroupPosition(MonsterGroupFactory factory, MonsterEnvironmentService environmentService, FightService fightService, MonsterGroupData data, SpawnCellSelector cellSelector, boolean fixed) {
        this.factory = factory;
        this.fightService = fightService;
        this.environmentService = environmentService;
        this.data = data;
        this.cellSelector = cellSelector;
        this.fixed = fixed;
    }

    /**
     * Link the group to the map and populate (spawn) with configured group data
     * This method should be called once, when exploration map is loaded
     *
     * @see fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded
     */
    public void populate(ExplorationMap map) {
        this.map = map;
        this.cellSelector.setMap(map);

        for (long i = groupStream().count(); i < data.maxCount(); ++i) {
            spawn();
        }
    }

    /**
     * Spawn a new group on the map
     *
     * Note: this method will not check the max count of monsters : if called manually, the group count can exceed max count
     */
    public void spawn() {
        map.add(factory.create(data, this));
    }

    /**
     * Get list of available monster groups on the map
     */
    public List<MonsterGroup> available() {
        if (map == null) {
            return Collections.emptyList();
        }

        return groupStream().collect(Collectors.toList());
    }

    /**
     * Find the group spawn cell
     *
     * If the cell is fixed (not -1 on {@link MonsterGroupPosition#position() cell}), this cell is returned
     * If not, a random free (without creatures, objects, and walkable) cell is returned
     *
     * @see fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell#free()
     * @see SpawnCellSelector#cell()
     */
    public ExplorationMapCell cell() {
        return cellSelector.cell();
    }

    /**
     * Does the current monster group is fixed ?
     * If true, the group cannot move
     */
    public boolean fixed() {
        return fixed;
    }

    /**
     * Start a fight with the group
     *
     * @param group The monster group. Must be handled by the current instance
     * @param player The player
     *
     * @return The created fight
     */
    public Fight startFight(MonsterGroup group, ExplorationPlayer player) {
        map.remove(group);
        environmentService.respawn(this, data.respawnTime());

        return fightService.handler(PvmBuilder.class).start(
            builder -> builder
                .randomize(!data.fixedTeamNumber())
                .map(map)
                .initiator(player.player())
                .monsterGroup(group)
        );
    }

    /**
     * Get stream of groups related to the current group position
     */
    private Stream<MonsterGroup> groupStream() {
        return map.creatures().stream()
            .filter(MonsterGroup.class::isInstance)
            .map(creature -> (MonsterGroup) creature)
            .filter(group -> this.equals(group.handler()))
        ;
    }
}
