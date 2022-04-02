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
import org.checkerframework.dataflow.qual.Deterministic;
import org.checkerframework.dataflow.qual.Pure;

import java.time.Duration;

/**
 * Fight type parameters
 */
public interface FightType {
    /**
     * The type id
     *
     * https://github.com/Emudofus/Dofus/blob/1.29/dofus/managers/GameManager.as#L1255
     */
    @Pure
    public int id();

    /**
     * Can cancel the fight without penalties
     */
    @Pure
    public boolean canCancel();

    /**
     * Does the fight type has a placement time limit ?
     *
     * @see FightType#placementDuration() For get the placement time limit
     */
    @Pure
    public boolean hasPlacementTimeLimit();

    /**
     * Get the fight placement duration
     * This value must be used only if hasPlacementTimeLimit is set to true
     *
     * @see FightType#hasPlacementTimeLimit()
     */
    @Pure
    public Duration placementDuration();

    /**
     * Get the maximum duration of a turn
     */
    @Pure
    public Duration turnDuration();

    /**
     * Get the rewards generator
     */
    @Deterministic
    public RewardsGenerator rewards();
}
