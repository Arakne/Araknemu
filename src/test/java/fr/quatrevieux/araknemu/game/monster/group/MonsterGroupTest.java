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

package fr.quatrevieux.araknemu.game.monster.group;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.map.event.CreatureMoving;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.environment.RandomCellSelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class MonsterGroupTest extends GameBaseCase {
    private MonsterGroup group;
    private LivingMonsterGroupPosition handler;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterSpells()
            .pushMonsterTemplates()
            .pushMaps()
            .pushAreas()
            .pushSubAreas()
        ;

        MonsterService service = container.get(MonsterService.class);

        map = container.get(ExplorationMapService.class).load(10340);
        group = new MonsterGroup(
            handler = new LivingMonsterGroupPosition(
                container.get(MonsterGroupFactory.class),
                container.get(MonsterEnvironmentService.class),
                container.get(FightService.class),
                new MonsterGroupData(3, Duration.ofMillis(60000), 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100), 1), new MonsterGroupData.Monster(34, new Interval(1, 100), 1), new MonsterGroupData.Monster(36, new Interval(1, 100), 1)), "", new Position(0, 0), false),
                new RandomCellSelector(), false
            ),
            5,
            Arrays.asList(
                service.load(31).all().get(2),
                service.load(34).all().get(3),
                service.load(36).all().get(1),
                service.load(36).all().get(5)
            ),
            Direction.WEST,
            map.get(123),
            new Position(10340, 125)
        );

        handler.populate(map);
        map.add(group);
    }

    @Test
    void values() {
        assertEquals(map.get(123), group.cell());
        assertEquals(Direction.WEST, group.orientation());
        assertCount(4, group.monsters());
        assertSame(handler, group.handler());
        assertEquals(new Position(10340, 125), group.winFightTeleportPosition());
    }

    @Test
    void sprite() {
        assertSame(group.sprite(), group.sprite());
        assertInstanceOf(MonsterGroupSprite.class, group.sprite());
        assertEquals("123;4;;-503;31,34,36,36;-3;1563^100,1568^100,1566^100,1566^100;4,9,2,6;-1,-1,-1;0,0,0,0;-1,-1,-1;0,0,0,0;-1,-1,-1;0,0,0,0;-1,-1,-1;0,0,0,0;", group.sprite().toString());
    }

    @Test
    void id() {
        assertEquals(-503, group.id());
    }

    @Test
    void apply() {
        Object o = new Object();
        Operation<Object> operation = Mockito.mock(Operation.class);
        Mockito.when(operation.onMonsterGroup(group)).thenReturn(o);

        assertSame(o, group.apply(operation));

        Mockito.verify(operation).onMonsterGroup(group);
    }

    @Test
    void startFight() throws SQLException {
        ExplorationPlayer player = explorationPlayer();
        player.changeMap(map, 123);

        Fight fight = group.startFight(player);

        assertFalse(map.creatures().contains(group));
        assertFalse(map.creatures().contains(player));

        assertCount(5, fight.fighters().all());
        assertContainsType(MonsterFighter.class, fight.fighters().all());
        assertContains(player.player().fighter(), fight.fighters().all());
        assertInstanceOf(PvmType.class, fight.type());
    }

    @Test
    void move() {
        AtomicReference<CreatureMoving> ref = new AtomicReference<>();
        map.dispatcher().add(CreatureMoving.class, ref::set);

        Path<ExplorationMapCell> path = new Path<>(
            new Decoder<>(map),
            Arrays.asList(
                new PathStep<>(map.get(123), Direction.EAST),
                new PathStep<>(map.get(138), Direction.SOUTH_EAST)
            )
        );

        group.move(path);
        assertEquals(map.get(138), group.cell());
        assertSame(group, ref.get().creature());
        assertSame(path, ref.get().path());
    }
}
