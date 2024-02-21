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

package fr.quatrevieux.araknemu.network.game.out.fight;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FightEndTest extends FightBaseCase {
    @Test
    void generateWithoutRewards() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        other.fighter().life().damage(player.fighter(), 1000);

        Fighter winner = player.fighter();
        Fighter looser = other.fighter();

        assertEquals(
            "GE" + fight.duration() + "|1|0|2;1;Bob;50;0;5350000;5481459;5860000;;;;;|0;2;Other;1;1;0;0;110;;;;;",
            new FightEnd(
                new FightRewardsSheet(
                    new EndFightResults(
                        fight,
                        Collections.singletonList(winner),
                        Collections.singletonList(looser)
                    ),
                    FightRewardsSheet.Type.NORMAL,
                    Arrays.asList(
                        new DropReward(RewardType.WINNER, winner, Collections.emptyList()),
                        new DropReward(RewardType.LOOSER, looser, Collections.emptyList())
                    )
                )
            ).toString()
        );
    }

    @Test
    void generateWithReward() throws Exception {
        Fight fight = createFight();
        fight.nextState();

        other.fighter().life().damage(player.fighter(), 1000);

        Fighter winner = player.fighter();
        Fighter looser = other.fighter();

        DropReward winnerReward = new DropReward(RewardType.WINNER, winner, Collections.emptyList());
        winnerReward.setKamas(250);
        winnerReward.setXp(1145);

        assertEquals(
            "GE" + fight.duration() + "|1|0|2;1;Bob;50;0;5350000;5481459;5860000;1145;;;;250|0;2;Other;1;1;0;0;110;;;;;",
            new FightEnd(
                new FightRewardsSheet(
                    new EndFightResults(
                        fight,
                        Collections.singletonList(winner),
                        Collections.singletonList(looser)
                    ),
                    FightRewardsSheet.Type.NORMAL,
                    Arrays.asList(winnerReward, new DropReward(RewardType.LOOSER, looser, Collections.emptyList()))
                )
            ).toString()
        );
    }
}