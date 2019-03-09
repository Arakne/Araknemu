package fr.quatrevieux.araknemu.game.fight.ending.reward.generator;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate rewards for challenge fight
 */
final public class ChallengeRewardsGenerator implements RewardsGenerator {
    @Override
    public FightRewardsSheet generate(EndFightResults results) {
        List<DropReward> rewards = new ArrayList<>();

        results.winners().stream().map(fighter -> new DropReward(RewardType.WINNER, fighter)).forEach(rewards::add);
        results.loosers().stream().map(fighter -> new DropReward(RewardType.LOOSER, fighter)).forEach(rewards::add);

        return new FightRewardsSheet(results, FightRewardsSheet.Type.NORMAL, rewards);
    }
}
