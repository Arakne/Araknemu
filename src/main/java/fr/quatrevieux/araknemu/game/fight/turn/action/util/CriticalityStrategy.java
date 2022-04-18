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

package fr.quatrevieux.araknemu.game.fight.turn.action.util;

import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Compute the criticality rate
 */
public interface CriticalityStrategy {
    /**
     * Compute the critical hit rate
     *
     * @param fighter Fighter performing the action
     * @param base The base criticality
     *
     * @return Get the critical hit rate. The lower value is 2 (1/2). Higher the value is, lower is the critical probability
     */
    public @Positive int hitRate(ActiveFighter fighter, @Positive int base);

    /**
     * Compute the critical failure rate
     *
     * @param fighter Fighter performing the action
     * @param base The base criticality
     *
     * @return Get the critical failure rate. The lower value is 2 (1/2).
     */
    public @Positive int failureRate(ActiveFighter fighter, @Positive int base);

    /**
     * Random check is it's a critical hit
     *
     * @param fighter Fighter performing the action
     * @param baseRate The base rate
     *
     * @return true on critical
     */
    public boolean hit(ActiveFighter fighter, @NonNegative int baseRate);

    /**
     * Random check is it's a critical failure
     *
     * @param fighter Fighter performing the action
     * @param baseRate The base rate
     *
     * @return true on failure
     */
    public boolean failed(ActiveFighter fighter, @NonNegative int baseRate);
}
