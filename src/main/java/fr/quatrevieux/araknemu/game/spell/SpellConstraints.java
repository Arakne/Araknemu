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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.spell;

import fr.arakne.utils.value.Interval;

/**
 * Constraints for launch a spell
 */
public interface SpellConstraints {
    /**
     * Get the spell range
     */
    public Interval range();

    /**
     * Spell should be launch in line
     */
    public boolean lineLaunch();

    /**
     * Launch block by line of sight
     */
    public boolean lineOfSight();

    /**
     * Needs a free cell
     */
    public boolean freeCell();

    /**
     * Maximum number of launch per turn
     */
    public int launchPerTurn();

    /**
     * Maximum number of launch per target per turn
     */
    public int launchPerTarget();

    /**
     * Number of turns between two launch
     */
    public int launchDelay();

    /**
     * List of require states
     */
    public int[] requiredStates();

    /**
     * List of forbidden states
     */
    public int[] forbiddenStates();
}
