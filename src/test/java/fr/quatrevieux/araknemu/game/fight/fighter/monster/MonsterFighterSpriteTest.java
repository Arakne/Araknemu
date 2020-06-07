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

package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.environment.RandomCellSelector;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.arakne.utils.maps.constant.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MonsterFighterSpriteTest extends FightBaseCase {
    private MonsterFighterSprite sprite;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
        ;

        MonsterService service = container.get(MonsterService.class);

        MonsterGroupTeam team = new MonsterGroupTeam(
            new MonsterGroup(
                new LivingMonsterGroupPosition(
                    container.get(MonsterGroupFactory.class),
                    container.get(MonsterEnvironmentService.class),
                    container.get(FightService.class),
                    new MonsterGroupData(3, Duration.ofMillis(60000), 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100), 1), new MonsterGroupData.Monster(34, new Interval(1, 100), 1), new MonsterGroupData.Monster(36, new Interval(1, 100), 1)), "", new Position(0, 0), false),
                    new RandomCellSelector()
                ),
                5,
                Collections.singletonList(service.load(31).all().get(2)),
                Direction.WEST,
                container.get(ExplorationMapService.class).load(10340).get(123),
                new Position(0, 0)
            ),
            Collections.singletonList(123),
            1
        );

        MonsterFighter fighter = (MonsterFighter) team.fighters().stream().findFirst().get();
        sprite = new MonsterFighterSprite(fighter, service.load(31).all().get(2));

        Fight fight = createFight();
        fighter.joinFight(fight, fight.map().get(123));
    }

    @Test
    void generate() {
        assertEquals("123;1;0;-1;31;-2;1563^100;3;-1;-1;-1;0,0,0,0;20;4;2;3;7;7;-7;-7;7;5;1", sprite.toString());
    }

    @Test
    void values() {
        assertEquals(-1, sprite.id());
        assertEquals(123, sprite.cell());
        assertEquals(Direction.SOUTH_EAST, sprite.orientation());
        assertEquals(Sprite.Type.MONSTER, sprite.type());
        assertEquals("31", sprite.name());
    }
}
