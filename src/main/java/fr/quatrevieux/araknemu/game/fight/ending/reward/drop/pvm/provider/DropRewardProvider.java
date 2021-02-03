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

package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider;

import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;

/**
 * Base type for provide drop rewards data
 */
public interface DropRewardProvider {
    /**
     * Initialize the provider for the given fight results
     */
    public Scope initialize(EndFightResults results);

    /**
     * Scoped provider for end fight results
     */
    public static interface Scope {
        /**
         * No-operation scope instance
         */
        public static final Scope NOOP = reward -> {};

        /**
         * Provide the reward data
         */
        public void provide(DropReward reward);
    }
}
