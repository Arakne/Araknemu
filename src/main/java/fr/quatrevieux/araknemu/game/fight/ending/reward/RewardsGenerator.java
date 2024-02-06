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
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Generate rewards for end fight
 */
public interface RewardsGenerator {
    /**
     * Generate the rewards from the end fight results
     *
     * @param results The end fight results
     */
    public FightRewardsSheet generate(EndFightResults results);

    /**
     * Check if the given fighter can be rewarded
     * Generally, it will check if the fighter is not a summon creature
     *
     * @param fighter The fighter to check
     *
     * @return true if the fighter can be rewarded (so it will be in the rewards sheet), false to ignore it
     */
    public boolean supports(Fighter fighter);
}
