package fr.quatrevieux.araknemu.game.fight.ending.reward.generator;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeRewardsGeneratorTest extends FightBaseCase {
    private Fight fight;
    private ChallengeRewardsGenerator generator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        generator = new ChallengeRewardsGenerator();
    }

    @Test
    void generate() {
        FightRewardsSheet sheet = generator.generate(
            new EndFightResults(fight, Collections.singletonList(player.fighter()), Collections.singletonList(other.fighter()))
        );

        assertSame(FightRewardsSheet.Type.NORMAL, sheet.type());
        assertSame(fight, sheet.results().fight());

        List<FightReward> rewards = new ArrayList<>(sheet.rewards());

        assertCount(2, rewards);
        assertContainsOnly(DropReward.class, rewards);

        assertSame(player.fighter(), rewards.get(0).fighter());
        assertEquals(RewardType.WINNER, rewards.get(0).type());
        assertEquals(0, DropReward.class.cast(rewards.get(0)).xp());
        assertEquals(0, DropReward.class.cast(rewards.get(0)).guildXp());
        assertEquals(0, DropReward.class.cast(rewards.get(0)).mountXp());
        assertEquals(0, DropReward.class.cast(rewards.get(0)).kamas());

        assertSame(other.fighter(), rewards.get(1).fighter());
        assertEquals(RewardType.LOOSER, rewards.get(1).type());
        assertEquals(0, DropReward.class.cast(rewards.get(1)).xp());
        assertEquals(0, DropReward.class.cast(rewards.get(1)).guildXp());
        assertEquals(0, DropReward.class.cast(rewards.get(1)).mountXp());
        assertEquals(0, DropReward.class.cast(rewards.get(1)).kamas());
    }
}
