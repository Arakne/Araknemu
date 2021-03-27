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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.type;

import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardsGenerator;

import java.time.Duration;

/**
 * Fight type for PvM
 */
public final class PvmType implements FightType {
    private final RewardsGenerator rewardsGenerator;
    private final GameConfiguration.FightConfiguration configuration;

    public PvmType(RewardsGenerator rewardsGenerator, GameConfiguration.FightConfiguration configuration) {
        this.rewardsGenerator = rewardsGenerator;
        this.configuration = configuration;
    }

    @Override
    public int id() {
        return 4;
    }

    @Override
    public boolean canCancel() {
        return false;
    }

    @Override
    public boolean hasPlacementTimeLimit() {
        return true;
    }

    @Override
    public Duration placementDuration() {
        return configuration.pvmPlacementDuration();
    }

    @Override
    public Duration turnDuration() {
        return configuration.turnDuration();
    }

    @Override
    public RewardsGenerator rewards() {
        return rewardsGenerator;
    }
}
