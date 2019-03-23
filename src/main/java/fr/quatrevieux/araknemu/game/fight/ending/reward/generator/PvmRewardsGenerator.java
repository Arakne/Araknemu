package fr.quatrevieux.araknemu.game.fight.ending.reward.generator;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropRewardAction;
import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.compute.KamasFormula;
import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.compute.XpFormula;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate rewards for PvM fight
 */
final public class PvmRewardsGenerator implements RewardsGenerator {
    final private List<DropRewardAction> winnerActions;
    final private List<DropRewardAction> looserActions;
    final private XpFormula xpFormula;
    final private KamasFormula kamasFormula;

    public PvmRewardsGenerator(List<DropRewardAction> winnerActions, List<DropRewardAction> looserActions, XpFormula xpFormula, KamasFormula kamasFormula) {
        this.winnerActions = winnerActions;
        this.looserActions = looserActions;
        this.xpFormula = xpFormula;
        this.kamasFormula = kamasFormula;
    }

    @Override
    public FightRewardsSheet generate(EndFightResults results) {
        List<DropReward> rewards = new ArrayList<>();

        final XpFormula.Scope xp = xpFormula.initialize(results);
        final KamasFormula.Scope kamas = kamasFormula.initialize(results);

        results.winners().stream().map(fighter -> new DropReward(RewardType.WINNER, fighter, winnerActions, xp.compute(fighter), kamas.compute(fighter))).forEach(rewards::add);
        results.loosers().stream().map(fighter -> new DropReward(RewardType.LOOSER, fighter, looserActions, 0, 0)).forEach(rewards::add);

        return new FightRewardsSheet(results, FightRewardsSheet.Type.NORMAL, rewards);
    }
}
