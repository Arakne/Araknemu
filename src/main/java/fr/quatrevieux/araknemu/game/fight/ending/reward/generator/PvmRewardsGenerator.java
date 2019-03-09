package fr.quatrevieux.araknemu.game.fight.ending.reward.generator;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropRewardAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate rewards for PvM fight
 */
final public class PvmRewardsGenerator implements RewardsGenerator {
    final private List<DropRewardAction> winnerActions;
    final private List<DropRewardAction> looserActions;

    public PvmRewardsGenerator(List<DropRewardAction> winnerActions, List<DropRewardAction> looserActions) {
        this.winnerActions = winnerActions;
        this.looserActions = looserActions;
    }

    @Override
    public FightRewardsSheet generate(EndFightResults results) {
        List<DropReward> rewards = new ArrayList<>();

        results.winners().stream().map(fighter -> new DropReward(RewardType.WINNER, fighter, winnerActions, 0, 0)).forEach(rewards::add);
        results.loosers().stream().map(fighter -> new DropReward(RewardType.LOOSER, fighter, looserActions, 0, 0)).forEach(rewards::add);

        return new FightRewardsSheet(results, FightRewardsSheet.Type.NORMAL, rewards);
    }
}
