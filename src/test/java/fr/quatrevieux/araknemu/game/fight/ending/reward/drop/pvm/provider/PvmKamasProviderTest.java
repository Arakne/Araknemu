package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PvmKamasProviderTest extends FightBaseCase {
    private PvmKamasProvider formula;
    private Fight fight;
    private List<Fighter> monsterFighters;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplates();

        formula = new PvmKamasProvider();
        fight = createPvmFight();

        monsterFighters = new ArrayList<>(fight.team(1).fighters());
    }

    @Test
    void withOneMonster() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );


        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);

        assertBetween(50, 70, reward.kamas());

        DropReward otherReward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(otherReward);

        assertNotEquals(reward.kamas(), otherReward.kamas());
    }

    @Test
    void withMultipleMonsters() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            monsterFighters
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);

        assertBetween(100, 140, reward.kamas());

        DropReward otherReward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(otherReward);

        assertNotEquals(reward.kamas(), otherReward.kamas());
    }

    @Test
    void withoutMonsters() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(player.fighter())
        );

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList());
        formula.initialize(results).provide(reward);

        assertEquals(0, reward.kamas());
    }
}
