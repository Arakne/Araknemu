package fr.quatrevieux.araknemu.game.fight.ending.reward.generator;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PvmRewardsGeneratorTest extends FightBaseCase {
    private PvmRewardsGenerator generator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        generator = new PvmRewardsGenerator(
            Collections.emptyList(),
            Collections.emptyList()
        );
    }

    @Test
    void generate() throws Exception {
        Fight fight = createPvmFight();
        fight.nextState();

        EndFightResults results = new EndFightResults(
            fight,
            new ArrayList<>(fight.team(0).fighters()),
            new ArrayList<>(fight.team(1).fighters())
        );

        FightRewardsSheet sheet = generator.generate(results);

        assertSame(results, sheet.results());
        assertCount(fight.fighters().size(), sheet.rewards());
        assertEquals(FightRewardsSheet.Type.NORMAL, sheet.type());

        assertEquals(RewardType.WINNER, sheet.rewards().get(0).type());
        assertEquals(player.fighter(), sheet.rewards().get(0).fighter());

        assertEquals(RewardType.LOOSER, sheet.rewards().get(1).type());
    }
}
