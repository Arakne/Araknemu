package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PvmBuilderTest extends GameBaseCase {
    private PvmBuilder builder;
    private MonsterGroup group;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushMonsterSpells()
            .pushMonsterTemplates()
        ;

        builder = new PvmBuilder(container.get(FightService.class), container.get(FighterFactory.class), new RandomUtil());

        MonsterService service = container.get(MonsterService.class);

        group = new MonsterGroup(
            new LivingMonsterGroupPosition(
                container.get(MonsterGroupFactory.class),
                container.get(FightService.class),
                new MonsterGroupPosition(new Position(10340, -1), 3),
                new MonsterGroupData(3, 60000, 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100)), new MonsterGroupData.Monster(34, new Interval(1, 100)), new MonsterGroupData.Monster(36, new Interval(1, 100))), "")
            ),
            5,
            Arrays.asList(
                service.load(31).all().get(2),
                service.load(34).all().get(3),
                service.load(36).all().get(1),
                service.load(36).all().get(5)
            ),
            Direction.WEST,
            123
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
        int playerTeamIsFirstTeam = 0;

        for (int i = 0; i < 100; ++i) {
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
}
