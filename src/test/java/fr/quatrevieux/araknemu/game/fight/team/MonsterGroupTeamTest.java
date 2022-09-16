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

package fr.quatrevieux.araknemu.game.fight.team;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.environment.RandomCellSelector;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.arakne.utils.maps.constant.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MonsterGroupTeamTest extends FightBaseCase {
    private MonsterGroupTeam team;
    private MonsterGroup group;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        MonsterService service = container.get(MonsterService.class);

        dataSet
            .pushMonsterSpells()
            .pushMonsterTemplates()
        ;

        FightMap map = loadFightMap(10340);

        team = new MonsterGroupTeam(
            group = new MonsterGroup(
                new LivingMonsterGroupPosition(
                    container.get(MonsterGroupFactory.class),
                    container.get(MonsterEnvironmentService.class),
                    container.get(FightService.class),
                    new MonsterGroupData(3, Duration.ofMillis(60000), 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100), 1), new MonsterGroupData.Monster(34, new Interval(1, 100), 1), new MonsterGroupData.Monster(36, new Interval(1, 100), 1)), "", new Position(0, 0), false),
                    new RandomCellSelector(), false
                ),
                5,
                Arrays.asList(
                    service.load(31).all().get(2),
                    service.load(34).all().get(3)
                ),
                Direction.WEST,
                container.get(ExplorationMapService.class).load(10340).get(123),
                new Position(0, 0)
            ),
            Arrays.asList(map.get(123), map.get(321)),
            1,
            container.get(FighterFactory.class)
        );
    }

    @Test
    void values() {
        assertNull(team.leader());
        assertEquals(-503, team.id());
        assertEquals(123, team.cell());
        assertEquals(1, team.type());
        assertEquals(Alignment.NONE, team.alignment());
        assertEquals(1, team.number());
        assertTrue(team.alive());
        assertSame(group, team.group());

        assertInstanceOf(DefaultTeamOptions.class, team.options());
        assertTrue(team.options().allowJoinTeam());
        assertTrue(team.options().allowSpectators());
        assertFalse(team.options().needHelp());
    }

    @Test
    void fighters() {
        assertContainsOnly(MonsterFighter.class, team.fighters());
        assertCount(2, team.fighters());
        assertArrayEquals(new int[] {-1, -2}, team.fighters().stream().mapToInt(Fighter::id).toArray());
        assertArrayEquals(new int[] {20, 65}, team.fighters().stream().mapToInt(fighter -> fighter.life().current()).toArray());
    }

    @Test
    void kick() {
        assertThrows(UnsupportedOperationException.class, () -> team.kick(Mockito.mock(Fighter.class)));
    }

    @Test
    void join() {
        assertThrows(JoinFightException.class, () -> team.join(Mockito.mock(Fighter.class)));
    }

    @Test
    void alive() throws Exception {
        Fight fight = createFight();

        int count = 0;

        for (Fighter fighter : team.fighters()) {
            fighter.joinFight(fight, team.startPlaces().get(count++));
            fighter.init();
        }

        assertTrue(team.alive());

        Fighter first = team.fighters().stream().findFirst().get();
        first.life().kill(first);

        assertTrue(team.alive());

        team.fighters().forEach(fighter -> fighter.life().kill(fighter));
        assertFalse(team.alive());
    }
}
