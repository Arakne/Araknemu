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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.util;

import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Common formulas for spell effects simulators
 */
public final class Formula {
    private Formula() {
        // Disable constructor
    }

    /**
     * Cap the duration to 10
     *
     * If the duration is 0, return 1
     * If the duration is -1 or greater than 10, return 10
     *
     * @param duration The duration, from {@link  fr.quatrevieux.araknemu.game.spell.effect.SpellEffect#duration()}. Can be -1 for infinite duration
     *
     * @return The capped duration. Will be between 1 and 10 inclusive
     */
    public static @Positive int capedDuration(@GTENegativeOne int duration) {
        return capedDuration(duration, 10);
    }

    /**
     * Cap the duration to the given max duration
     *
     * If the duration is 0, return 1
     * If the duration is -1 or greater than max duration, return max duration
     *
     * @param duration The duration, from {@link  fr.quatrevieux.araknemu.game.spell.effect.SpellEffect#duration()}. Can be -1 for infinite duration
     * @param maxDuration The max duration
     *
     * @return The capped duration. Will be between 1 and maxDuration inclusive
     */
    public static @Positive int capedDuration(@GTENegativeOne int duration, @Positive int maxDuration) {
        if (duration == -1 || duration > maxDuration) {
            return maxDuration;
        }

        return Math.max(duration, 1);
    }
}
