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

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.activity.ActivityService;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MoveMonstersTest extends GameBaseCase {
    private MoveMonsters task;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
            .pushMonsterGroups()
            .pushMaps()
            .pushAreas()
            .pushSubAreas()
        ;

        task = new MoveMonsters(
            container.get(MonsterEnvironmentService.class),
            Duration.ofSeconds(10),
            100,
            5
        );
    }

    @Test
    void getters() {
        assertEquals(Duration.ofSeconds(10), task.delay());
        assertFalse(task.retry(container.get(ActivityService.class)));
        assertEquals("Move monsters", task.toString());
    }

    @Test
    void executeWithoutMonsters() {
        task.execute(container.get(Logger.class));
    }

    @Test
    void moveSingleGroup() throws SQLException {
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 3));
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        explorationPlayer().changeMap(map, 123);
        requestStack.clear();

        MonsterGroup group = container.get(MonsterEnvironmentService.class).byMap(10340).stream().findFirst().get().available().get(0);
        ExplorationMapCell lastCell = group.cell();

        task.execute(container.get(Logger.class));

        assertNotEquals(lastCell, group.cell());
        assertBetween(0, 5, group.cell().coordinate().distance(lastCell.coordinate()));
        requestStack.assertLast(new GameActionResponse("", ActionType.MOVE, group.id(), "acRhcD"));
    }

    @Test
    void fixedGroupShouldNotMove() throws SQLException {
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, 123), 3));
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        explorationPlayer().changeMap(map, 123);
        requestStack.clear();

        MonsterGroup group = container.get(MonsterEnvironmentService.class).byMap(10340).stream().findFirst().get().available().get(0);

        task.execute(container.get(Logger.class));

        assertEquals(map.get(123), group.cell());
        requestStack.assertEmpty();
    }

    @Test
    void moveOnlyOneGroupPerMap() throws SQLException {
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 2));
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        explorationPlayer().changeMap(map, 123);
        requestStack.clear();

        LivingMonsterGroupPosition position = container.get(MonsterEnvironmentService.class).byMap(10340).stream().findFirst().get();
        Collection<ExplorationMapCell> baseCells = position.available().stream().map(MonsterGroup::cell).collect(Collectors.toList());

        task.execute(container.get(Logger.class));

        // Diff on current cells
        baseCells.removeAll(position.available().stream().map(MonsterGroup::cell).collect(Collectors.toList()));
        assertCount(1, baseCells); // Count the changed cells
    }

    @Test
    void moveChance() throws SQLException {
        task = new MoveMonsters(container.get(MonsterEnvironmentService.class), Duration.ofSeconds(10), 25, 5);

        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 3));
        container.get(ExplorationMapService.class).load(10340);

        MonsterGroup group = container.get(MonsterEnvironmentService.class).byMap(10340).stream().findFirst().get().available().get(0);

        int moveCount = 0;

        for (int i = 0; i < 100; ++i) {
            ExplorationMapCell lastCell = group.cell();

            task.execute(container.get(Logger.class));

            if (!group.cell().equals(lastCell)) {
                ++moveCount;
            }

            assertBetween(0, 5, group.cell().coordinate().distance(lastCell.coordinate()));
        }

        assertBetween(15, 35, moveCount);
    }

    @Test
    void moveDistance() throws SQLException {
        task = new MoveMonsters(container.get(MonsterEnvironmentService.class), Duration.ofSeconds(10), 100, 20);

        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 3));
        container.get(ExplorationMapService.class).load(10340);

        MonsterGroup group = container.get(MonsterEnvironmentService.class).byMap(10340).stream().findFirst().get().available().get(0);

        int maxDistance = 0;

        for (int i = 0; i < 100; ++i) {
            ExplorationMapCell lastCell = group.cell();

            task.execute(container.get(Logger.class));

            int distance = group.cell().coordinate().distance(lastCell.coordinate());

            if (distance > maxDistance) {
                maxDistance = distance;
            }
        }

        assertEquals(20, maxDistance);
    }

    @Test
    void moveWithoutFreeCellShouldNotMove() throws SQLException {
        task = new MoveMonsters(container.get(MonsterEnvironmentService.class), Duration.ofSeconds(10), 100, 5);

        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(new Position(10340, -1), 3));
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        MonsterGroup group = container.get(MonsterEnvironmentService.class).byMap(10340).stream().findFirst().get().available().get(0);

        for (int cellId = 0; cellId < map.size(); ++cellId) {
            map.add(new FakeCreature(map.get(cellId)));
        }

        ExplorationMapCell lastCell = group.cell();
        task.execute(container.get(Logger.class));

        assertEquals(lastCell, group.cell());
    }

    class FakeCreature implements ExplorationCreature {
        final private ExplorationMapCell cell;

        public FakeCreature(ExplorationMapCell cell) {
            this.cell = cell;
        }

        @Override
        public void apply(Operation operation) {

        }

        @Override
        public Sprite sprite() {
            return null;
        }

        @Override
        public int id() {
            return 1000 + cell.id();
        }

        @Override
        public ExplorationMapCell cell() {
            return cell;
        }

        @Override
        public Direction orientation() {
            return null;
        }
    }
}
