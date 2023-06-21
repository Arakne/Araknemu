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

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action.AddExperience;
import fr.quatrevieux.araknemu.game.fight.event.FightFinished;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ApplyEndFightRewardTest extends FightBaseCase {
    private Fight fight;
    private ApplyEndFightReward listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        listener = new ApplyEndFightReward(player.fighter());
    }

    @Test
    void onFightFinishedWithoutReward() {
        listener.on(new FightFinished(new DropReward(RewardType.WINNER, player.fighter(), Collections.emptyList())));

        assertFalse(player.isFighting());
    }

    @Test
    void onFightFinishedWithXpReward() {
        long lastXp = player.properties().experience().current();

        DropReward reward = new DropReward(RewardType.WINNER, player.fighter(), Arrays.asList(new AddExperience()));
        reward.setXp(1000);

        listener.on(new FightFinished(reward));

        assertFalse(player.isFighting());
        assertEquals(lastXp + 1000, player.properties().experience().current());
    }
}
