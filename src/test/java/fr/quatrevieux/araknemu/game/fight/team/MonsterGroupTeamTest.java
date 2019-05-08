package fr.quatrevieux.araknemu.game.fight.team;

import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.RandomCellSelector;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MonsterGroupTeamTest extends FightBaseCase {
    private MonsterGroupTeam team;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        MonsterService service = container.get(MonsterService.class);

        dataSet
            .pushMonsterSpells()
            .pushMonsterTemplates()
        ;

        team = new MonsterGroupTeam(
            new MonsterGroup(
                new LivingMonsterGroupPosition(
                    container.get(MonsterGroupFactory.class),
                    container.get(FightService.class),
                    new MonsterGroupData(3, 60000, 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100)), new MonsterGroupData.Monster(34, new Interval(1, 100)), new MonsterGroupData.Monster(36, new Interval(1, 100))), ""),
                    new RandomCellSelector()
                ),
                5,
                Arrays.asList(
                    service.load(31).all().get(2),
                    service.load(34).all().get(3)
                ),
                Direction.WEST,
                container.get(ExplorationMapService.class).load(10340).get(123)
            ),
            Arrays.asList(123, 321),
            1
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
            fighter.joinFight(fight, fight.map().get(team.startPlaces().get(count++)));
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
