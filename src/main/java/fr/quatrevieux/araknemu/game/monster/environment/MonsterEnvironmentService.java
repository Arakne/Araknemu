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

package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupPositionRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.activity.ActivityService;
import fr.quatrevieux.araknemu.game.activity.SimpleTask;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.listener.map.monster.LaunchMonsterFight;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handle environment interactions with monsters
 */
public final class MonsterEnvironmentService implements EventsSubscriber, PreloadableService {
    private final ActivityService activityService;
    private final FightService fightService;
    private final MonsterGroupFactory factory;
    private final MonsterGroupPositionRepository positionRepository;
    private final MonsterGroupDataRepository dataRepository;
    private final GameConfiguration.ActivityConfiguration configuration;

    /**
     * Groups indexed by map id
     */
    private final ConcurrentMap<Integer, Collection<LivingMonsterGroupPosition>> groupsByMap = new ConcurrentHashMap<>();

    /**
     * Groups are preloaded ?
     * If true, consider that all data are loaded, so loading maps without loaded groups do not execute a query
     */
    private boolean preloaded = false;

    public MonsterEnvironmentService(ActivityService activityService, FightService fightService, MonsterGroupFactory factory, MonsterGroupPositionRepository positionRepository, MonsterGroupDataRepository dataRepository, GameConfiguration.ActivityConfiguration configuration) {
        this.activityService = activityService;
        this.fightService = fightService;
        this.factory = factory;
        this.positionRepository = positionRepository;
        this.dataRepository = dataRepository;
        this.configuration = configuration;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading monster groups data...");

        final Map<Integer, MonsterGroupData> groupsData = dataRepository.all().stream()
            .collect(Collectors.toMap(MonsterGroupData::id, Function.identity()))
        ;

        logger.info("{} monster groups loaded", groupsData.size());

        logger.info("Loading monster groups positions...");

        for (MonsterGroupPosition position : positionRepository.all()) {
            groupsByMap
                .computeIfAbsent(position.position().map(), mapId -> new ArrayList<>())
                .add(new LivingMonsterGroupPosition(
                    factory,
                    this,
                    fightService,
                    groupsData.get(position.groupId()),
                    SpawnCellSelector.forPosition(position.position())
                ))
            ;
        }

        logger.info("{} Map positions loaded", groupsByMap.size());

        preloaded = true;

        activityService.periodic(
            new MoveMonsters(
                this,
                Duration.ofSeconds(configuration.monsterMoveInterval()),
                configuration.monsterMovePercent()
            )
        );
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<MapLoaded>() {
                @Override
                public void on(MapLoaded event) {
                    byMap(event.map().id()).forEach(group -> group.populate(event.map()));
                    event.map().dispatcher().add(new LaunchMonsterFight());
                }

                @Override
                public Class<MapLoaded> event() {
                    return MapLoaded.class;
                }
            },
        };
    }

    /**
     * Get list of groups for a map
     *
     * @param mapId The map id to load
     */
    public Collection<LivingMonsterGroupPosition> byMap(int mapId) {
        if (groupsByMap.containsKey(mapId)) {
            return groupsByMap.get(mapId);
        }

        if (preloaded) {
            return Collections.emptyList();
        }

        final Collection<LivingMonsterGroupPosition> groups = new ArrayList<>();

        for (MonsterGroupPosition position : positionRepository.byMap(mapId)) {
            groups.add(new LivingMonsterGroupPosition(
                factory,
                this,
                fightService,
                dataRepository.get(position.groupId()),
                SpawnCellSelector.forPosition(position.position())
            ));
        }

        groupsByMap.put(mapId, groups);

        return groups;
    }

    /**
     * Put the respawn to the activity service
     */
    void respawn(LivingMonsterGroupPosition position, Duration delay) {
        activityService.execute(
            new SimpleTask(logger -> position.spawn())
                .setDelay(delay.dividedBy(configuration.monsterRespawnSpeedFactor()))
                .setMaxTries(2)
                .setName("Respawn")
        );
    }

    /**
     * Get all loaded groups
     */
    Stream<LivingMonsterGroupPosition> groups() {
        return groupsByMap.values().stream().flatMap(Collection::stream);
    }
}
