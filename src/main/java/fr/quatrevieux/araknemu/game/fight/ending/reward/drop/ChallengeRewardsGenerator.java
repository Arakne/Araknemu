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

package fr.quatrevieux.araknemu.game.fight.ending.reward.drop;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardsGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generate rewards for challenge fight
 */
public final class ChallengeRewardsGenerator implements RewardsGenerator {
    @Override
    public FightRewardsSheet generate(EndFightResults results) {
        final List<DropReward> rewards = new ArrayList<>();

        results.winners().stream().map(fighter -> new DropReward(RewardType.WINNER, fighter, Collections.emptyList())).forEach(rewards::add);
        results.loosers().stream().map(fighter -> new DropReward(RewardType.LOOSER, fighter, Collections.emptyList())).forEach(rewards::add);

        return new FightRewardsSheet(results, FightRewardsSheet.Type.NORMAL, rewards);
    }
}
