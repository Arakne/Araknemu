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

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupPositionRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.activity.ActivityService;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonsterEnvironmentServiceTest extends GameBaseCase {
    private MonsterEnvironmentService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushAreas()
            .pushSubAreas()
            .pushMonsterGroups()
            .pushMonsterSpells()
            .pushMonsterTemplates()
        ;

        service = new MonsterEnvironmentService(
            container.get(ActivityService.class),
            container.get(FightService.class),
            container.get(MonsterGroupFactory.class),
            container.get(MonsterGroupPositionRepository.class),
            container.get(MonsterGroupDataRepository.class),
            container.get(GameConfiguration.class).activity()
        );
    }

    @Test
    void onMapLoadedShouldSpawnMonsterGroups() throws SQLException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 1));

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new MapLoaded(map));

        Collection<ExplorationCreature> creatures = map.creatures();

        assertCount(2, creatures);
        assertContainsOnly(MonsterGroup.class, creatures);
    }

    @Test
    void byMap() throws SQLException {
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 1));
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10300, 123), 2));
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10300, 125), 2));

        assertCount(0, service.byMap(123));
        assertCount(1, service.byMap(10340));
        assertCount(2, service.byMap(10300));

        assertSame(service.byMap(10340), service.byMap(10340));
    }

    @Test
    void preload() throws SQLException {
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 1));
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10300, 123), 2));
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10300, 125), 2));

        Logger logger = Mockito.mock(Logger.class);

        service.preload(logger);

        Mockito.verify(logger).info("Loading monster groups data...");
        Mockito.verify(logger).info("{} monster groups loaded", 3);
        Mockito.verify(logger).info("Loading monster groups positions...");
        Mockito.verify(logger).info("{} Map positions loaded", 2);

        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10540, -1), 1));
        assertCount(0, service.byMap(10540));
        assertCount(1, service.byMap(10340));
    }

    @RepeatedIfExceptionsTest
    void respawn() throws InterruptedException, SQLException {
        LivingMonsterGroupPosition monsterGroupPosition = new LivingMonsterGroupPosition(
            container.get(MonsterGroupFactory.class),
            container.get(MonsterEnvironmentService.class),
            container.get(FightService.class),
            container.get(MonsterGroupDataRepository.class).get(3),
            new RandomCellSelector()
        );

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        monsterGroupPosition.populate(map);

        explorationPlayer().join(map);
        requestStack.clear();

        service.respawn(monsterGroupPosition, Duration.ZERO);

        Thread.sleep(50);

        MonsterGroup lastGroup = monsterGroupPosition.available().get(1);

        requestStack.assertLast(new AddSprites(Collections.singleton(lastGroup.sprite())));
    }

    @RepeatedIfExceptionsTest
    void respawnWithDelay() throws InterruptedException, SQLException {
        LivingMonsterGroupPosition monsterGroupPosition = new LivingMonsterGroupPosition(
            container.get(MonsterGroupFactory.class),
            container.get(MonsterEnvironmentService.class),
            container.get(FightService.class),
            container.get(MonsterGroupDataRepository.class).get(3),
            new RandomCellSelector()
        );

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        monsterGroupPosition.populate(map);
        int size = monsterGroupPosition.available().size();

        explorationPlayer().join(map);
        requestStack.clear();

        service.respawn(monsterGroupPosition, Duration.ofMillis(50));
        Thread.sleep(40);

        assertEquals(size, monsterGroupPosition.available().size());
        Thread.sleep(20);

        MonsterGroup lastGroup = monsterGroupPosition.available().get(1);

        requestStack.assertLast(new AddSprites(Collections.singleton(lastGroup.sprite())));
    }

    @RepeatedIfExceptionsTest
    void respawnWithDelayAndRespawnSpeedFactor() throws InterruptedException, SQLException {
        setConfigValue("activity.monsters.respawnSpeedFactor", "4");
        LivingMonsterGroupPosition monsterGroupPosition = new LivingMonsterGroupPosition(
            container.get(MonsterGroupFactory.class),
            container.get(MonsterEnvironmentService.class),
            container.get(FightService.class),
            container.get(MonsterGroupDataRepository.class).get(3),
            new RandomCellSelector()
        );

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        monsterGroupPosition.populate(map);

        explorationPlayer().join(map);
        requestStack.clear();

        service.respawn(monsterGroupPosition, Duration.ofMillis(50));
        Thread.sleep(40);

        MonsterGroup lastGroup = monsterGroupPosition.available().get(1);

        requestStack.assertLast(new AddSprites(Collections.singleton(lastGroup.sprite())));
    }

    @Test
    void groups() throws SQLException {
        assertEquals(0, service.groups().count());

        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 1));
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10300, 123), 2));
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10300, 125), 2));

        service.preload(container.get(Logger.class));
        assertEquals(3, service.groups().count());
    }
}
