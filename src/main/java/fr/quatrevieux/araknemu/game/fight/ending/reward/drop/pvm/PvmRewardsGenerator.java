package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardsGenerator;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.DropRewardAction;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider.DropRewardProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generate rewards for PvM fight
 */
final public class PvmRewardsGenerator implements RewardsGenerator {
    final private List<DropRewardAction> winnerActions;
    final private List<DropRewardAction> looserActions;
    final private List<DropRewardProvider> providers;

    public PvmRewardsGenerator(List<DropRewardAction> winnerActions, List<DropRewardAction> looserActions, List<DropRewardProvider> providers) {
        this.winnerActions = winnerActions;
        this.looserActions = looserActions;
        this.providers = providers;
    }

    @Override
    public FightRewardsSheet generate(EndFightResults results) {
        List<DropReward> rewards = new ArrayList<>();

        final List<DropRewardProvider.Scope> providerScopes = providers.stream()
            .map(provider -> provider.initialize(results))
            .collect(Collectors.toList())
        ;

        // Sort winners by discernment
        results.winners().stream()
            .sorted((o1, o2) -> o2.characteristics().discernment() - o1.characteristics().discernment())
            .map(fighter -> {
                DropReward reward = new DropReward(RewardType.WINNER, fighter, winnerActions);

                providerScopes.forEach(scope -> scope.provide(reward));

                return reward;
            })
            .forEach(rewards::add)
        ;

        results.loosers().stream()
            .map(fighter -> new DropReward(RewardType.LOOSER, fighter, looserActions))
            .forEach(rewards::add)
        ;

        return new FightRewardsSheet(results, FightRewardsSheet.Type.NORMAL, rewards);
    }
}
