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

package fr.quatrevieux.araknemu.game.fight.type;

import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardsGenerator;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.ChallengeRewardsGenerator;

import java.time.Duration;

/**
 * Fight type for challenge
 */
final public class ChallengeType implements FightType {
    @Override
    public int id() {
        return 0;
    }

    @Override
    public boolean canCancel() {
        return true;
    }

    @Override
    public boolean hasPlacementTimeLimit() {
        return false;
    }

    @Override
    public int placementTime() {
        return 0;
    }

    @Override
    public Duration turnDuration() {
        return Duration.ofSeconds(30); // @todo configuration
    }

    @Override
    public RewardsGenerator rewards() {
        return new ChallengeRewardsGenerator();
    }
}
