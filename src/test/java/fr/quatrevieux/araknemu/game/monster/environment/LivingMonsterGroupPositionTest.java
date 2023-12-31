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

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LivingMonsterGroupPositionTest extends GameBaseCase {
    private ExplorationMap map;
    private LivingMonsterGroupPosition monsterGroupPosition;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushAreas()
            .pushSubAreas()
            .pushMonsterTemplates()
            .pushMonsterGroups()
            .pushMonsterSpells()
        ;

        map = container.get(ExplorationMapService.class).load(10340);

        monsterGroupPosition = new LivingMonsterGroupPosition(
            container.get(MonsterGroupFactory.class),
            container.get(MonsterEnvironmentService.class),
            container.get(FightService.class),
            container.get(MonsterGroupDataRepository.class).get(1),
            new RandomCellSelector(), false
        );
    }

    @Test
    void populate() {
        assertCount(0, map.creatures());

        monsterGroupPosition.populate(map);
        assertCount(2, map.creatures());

        monsterGroupPosition.populate(map);
        assertCount(2, map.creatures());
    }

    @Test
    void available() {
        monsterGroupPosition.populate(map);

        assertArrayEquals(
            map.creatures().toArray(),
            monsterGroupPosition.available().toArray()
        );
    }

    @Test
    void spawn() {
        monsterGroupPosition.populate(map);
        assertCount(2, monsterGroupPosition.available());

        monsterGroupPosition.spawn();
        assertCount(3, monsterGroupPosition.available());
    }

    @Test
    void respawn() {
        monsterGroupPosition.populate(map);
        map.remove(monsterGroupPosition.available().get(0));
        assertCount(1, monsterGroupPosition.available());

        monsterGroupPosition.respawn();
        assertCount(2, monsterGroupPosition.available());

        monsterGroupPosition.respawn();
        assertCount(2, monsterGroupPosition.available());
    }

    @Test
    void mapNotLoaded() {
        assertThrows(IllegalStateException.class, monsterGroupPosition::spawn);
        assertThrows(IllegalStateException.class, monsterGroupPosition::respawn);
    }

    @Test
    void fixedGroup() {
        monsterGroupPosition = new LivingMonsterGroupPosition(
            container.get(MonsterGroupFactory.class),
            container.get(MonsterEnvironmentService.class),
            container.get(FightService.class),
            new MonsterGroupData(3, Duration.ZERO, 0, 1, Arrays.asList(
                new MonsterGroupData.Monster(36, new Interval(5, 5), 1),
                new MonsterGroupData.Monster(31, new Interval(2, 2), 1)
            ), "", new Position(0, 0), false),
            new FixedCellSelector(123), true
        );

        monsterGroupPosition.populate(map);

        assertTrue(monsterGroupPosition.fixed());
        assertEquals(map.get(123), monsterGroupPosition.cell());

        assertCount(1, monsterGroupPosition.available());
        MonsterGroup group = monsterGroupPosition.available().get(0);

        assertEquals(map.get(123), group.cell());
        assertEquals(36, group.monsters().get(0).id());
        assertEquals(5, group.monsters().get(0).level());
        assertEquals(31, group.monsters().get(1).id());
        assertEquals(2, group.monsters().get(1).level());
    }

    @Test
    void cell() {
        monsterGroupPosition.populate(map);

        assertNotEquals(monsterGroupPosition.cell(), monsterGroupPosition.cell());
        assertTrue(monsterGroupPosition.cell().free());
    }

    @Test
    void startFight() throws SQLException {
        monsterGroupPosition.populate(map);

        ExplorationPlayer player = explorationPlayer();
        player.changeMap(map, 123);

        MonsterGroup group = monsterGroupPosition.available().get(0);
        Fight fight = monsterGroupPosition.startFight(group, player);

        assertFalse(map.creatures().contains(group));
        assertFalse(map.creatures().contains(player));

        assertCount(group.monsters().size() + 1, fight.fighters().all());
        assertContainsType(MonsterFighter.class, fight.fighters().all());
        assertContains(player.player().fighter(), fight.fighters().all());
        assertInstanceOf(PvmType.class, fight.type());
    }

    @Test
    void startFightShouldReturnNullIfGroupIsNotOnMap() throws SQLException {
        monsterGroupPosition.populate(map);

        ExplorationPlayer player = explorationPlayer();
        player.changeMap(map, 123);

        MonsterGroup group = monsterGroupPosition.available().get(0);

        map.remove(group);

        assertNull(monsterGroupPosition.startFight(group, player));
    }

    @Test
    void startFightCannotBeCalledTwice() throws SQLException {
        monsterGroupPosition.populate(map);

        ExplorationPlayer player = explorationPlayer();
        player.changeMap(map, 123);

        MonsterGroup group = monsterGroupPosition.available().get(0);
        assertNotNull(monsterGroupPosition.startFight(group, player));

        assertNull(monsterGroupPosition.startFight(group, player));
    }

    @Test
    void startFightWithFixedTeamNumber() throws SQLException {
        monsterGroupPosition = new LivingMonsterGroupPosition(
            container.get(MonsterGroupFactory.class),
            container.get(MonsterEnvironmentService.class),
            container.get(FightService.class),
            container.get(MonsterGroupDataRepository.class).get(2),
            new RandomCellSelector(), false
        );

        monsterGroupPosition.populate(map);

        ExplorationPlayer player = explorationPlayer();
        player.changeMap(map, 123);

        MonsterGroup group = monsterGroupPosition.available().get(0);
        Fight fight = monsterGroupPosition.startFight(group, player);

        assertInstanceOf(SimpleTeam.class, fight.team(0));
        assertInstanceOf(MonsterGroupTeam.class, fight.team(1));
    }

    @RepeatedIfExceptionsTest
    void startFightShouldRespawnGroup() throws SQLException, InterruptedException {
        monsterGroupPosition = new LivingMonsterGroupPosition(
            container.get(MonsterGroupFactory.class),
            container.get(MonsterEnvironmentService.class),
            container.get(FightService.class),
            container.get(MonsterGroupDataRepository.class).get(3),
            new RandomCellSelector(), false
        );

        monsterGroupPosition.populate(map);


        ExplorationPlayer player = explorationPlayer();
        player.changeMap(map, 123);

        MonsterGroup group = monsterGroupPosition.available().get(0);
        monsterGroupPosition.startFight(group, player);

        assertEquals(0, monsterGroupPosition.available().size());

        Thread.sleep(200);
        assertEquals(1, monsterGroupPosition.available().size());
    }

    @Test
    void availableWithoutPopulate() {
        assertCount(0, monsterGroupPosition.available());
    }
}
