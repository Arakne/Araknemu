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

package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.environment.RandomCellSelector;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PvmBuilderTest extends GameBaseCase {
    private PvmBuilder builder;
    private MonsterGroup group;

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

        builder = new PvmBuilder(
            container.get(FightService.class),
            container.get(FighterFactory.class),
            new RandomUtil(),
            container.get(PvmType.class),
            container.get(Logger.class)
        );

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
    }

    @Test
    void build() throws Exception {
        Fight fight = builder
            .initiator(gamePlayer())
            .monsterGroup(group)
            .map(container.get(ExplorationMapService.class).load(10340))
            .build(1)
        ;

        assertInstanceOf(PvmType.class, fight.type());
        assertCount(2, fight.teams());
        assertCount(5, fight.fighters(false));
        assertContainsType(MonsterGroupTeam.class, fight.teams());
        assertContainsType(SimpleTeam.class, fight.teams());
        assertEquals(1, fight.id());
    }

    @Test
    void buildTeamOrderShouldBeRandomized() throws Exception {
        RandomUtil random = new RandomUtil();

        int playerTeamIsFirstTeam = 0;

        for (int i = 0; i < 100; ++i) {
            builder = new PvmBuilder(
                container.get(FightService.class),
                container.get(FighterFactory.class),
                random,
                container.get(PvmType.class),
                container.get(Logger.class)
            );

            Fight fight = builder
                .initiator(gamePlayer())
                .monsterGroup(group)
                .map(container.get(ExplorationMapService.class).load(10340))
                .build(1)
            ;

            if (fight.team(0) instanceof SimpleTeam) {
                ++playerTeamIsFirstTeam;
            }
        }

        assertBetween(40, 60, playerTeamIsFirstTeam);
    }

    @Test
    void buildNotRandomized() throws Exception {
        builder = new PvmBuilder(
            container.get(FightService.class),
            container.get(FighterFactory.class),
            null,
            container.get(PvmType.class),
            container.get(Logger.class)
        );

        for (int i = 0; i < 100; ++i) {
            Fight fight = builder
                .initiator(gamePlayer())
                .monsterGroup(group)
                .map(container.get(ExplorationMapService.class).load(10340))
                .build(1)
            ;

            assertTrue(fight.team(0) instanceof SimpleTeam);
        }
    }
}
