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

package fr.quatrevieux.araknemu.game.fight.ending.reward;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;

import java.util.List;

/**
 * Contains all end fight rewards
 */
public final class FightRewardsSheet {
    public enum Type {
        /** For drop, pvm, challenge...) */
        NORMAL,
        /** For honour fight (conquest, PvP) */
        HONOUR
    }

    private final EndFightResults results;
    private final Type type;
    private final List<DropReward> rewards;

    public FightRewardsSheet(EndFightResults results, Type type, List<DropReward> rewards) {
        this.results = results;
        this.type = type;
        this.rewards = rewards;
    }

    /**
     * Get the end fight results
     */
    public EndFightResults results() {
        return results;
    }

    /**
     * Get the reward type
     */
    public Type type() {
        return type;
    }

    /**
     * Get all rewards
     */
    public List<DropReward> rewards() {
        return rewards;
    }
}
