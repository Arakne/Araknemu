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

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.environment.RandomCellSelector;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonsterGroupSpriteTest extends GameBaseCase {
    private MonsterGroup group;
    private MonsterGroupSprite sprite;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushAreas()
            .pushSubAreas()
            .pushMonsterSpells()
            .pushMonsterTemplates()
        ;

        MonsterService service = container.get(MonsterService.class);

        group = new MonsterGroup(
            new LivingMonsterGroupPosition(
                container.get(MonsterGroupFactory.class),
                container.get(MonsterEnvironmentService.class),
                container.get(FightService.class),
                new MonsterGroupData(3, Duration.ofMillis(60000), 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100), 1), new MonsterGroupData.Monster(34, new Interval(1, 100), 1), new MonsterGroupData.Monster(36, new Interval(1, 100), 1)), "", new Position(0, 0), false),
                new RandomCellSelector()
            ),
            5,
            Arrays.asList(
                service.load(31).all().get(2),
                service.load(34).all().get(3),
                service.load(36).all().get(1),
                service.load(36).all().get(5)
            ),
            Direction.WEST,
            container.get(ExplorationMapService.class).load(10340).get(123),
            new Position(0, 0)
        );

        sprite = new MonsterGroupSprite(group);
    }

    @Test
    void generate() {
        assertEquals("123;4;;-503;31,34,36,36;-3;1563^100,1568^100,1566^100,1566^100;4,9,2,6;-1,-1,-1;0,0,0,0;-1,-1,-1;0,0,0,0;-1,-1,-1;0,0,0,0;-1,-1,-1;0,0,0,0;", sprite.toString());
    }

    @Test
    void values() {
        assertEquals(-503, sprite.id());
        assertEquals(123, sprite.cell());
        assertEquals(Direction.WEST, sprite.orientation());
        assertEquals(Sprite.Type.MONSTER_GROUP, sprite.type());
        assertEquals("31,34,36,36", sprite.name());
    }
}
