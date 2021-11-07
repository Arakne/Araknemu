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
public final class PvmRewardsGenerator implements RewardsGenerator {
    private final List<DropRewardAction> winnerActions;
    private final List<DropRewardAction> looserActions;
    private final List<DropRewardProvider> providers;

    public PvmRewardsGenerator(List<DropRewardAction> winnerActions, List<DropRewardAction> looserActions, List<DropRewardProvider> providers) {
        this.winnerActions = winnerActions;
        this.looserActions = looserActions;
        this.providers = providers;
    }

    @Override
    public FightRewardsSheet generate(EndFightResults results) {
        final List<DropReward> rewards = new ArrayList<>();
        final List<DropRewardProvider.Scope> providerScopes = providers.stream()
            .map(provider -> provider.initialize(results))
            .collect(Collectors.toList())
        ;

        // Sort winners by discernment
        results.winners().stream()
            .sorted((o1, o2) -> o2.characteristics().discernment() - o1.characteristics().discernment())
            .map(fighter -> {
                final DropReward reward = new DropReward(RewardType.WINNER, fighter, winnerActions);

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
