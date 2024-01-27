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

package fr.quatrevieux.araknemu.game.fight.castable.effect;

import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Utility class for effects
 *
 * @todo Should be removed or refactored with Effect enum
 */
public final class EffectsUtils {
    /**
     * Disable constructor
     */
    private EffectsUtils() {}

    /**
     * Check if the effect is a damage effect
     *
     * @param id The effect id
     */
    public static boolean isDamageEffect(int id) {
        return
            id == 82
            || (id >= 85 && id <= 89)
            || (id >= 91 && id <= 100)
            || id == 144
        ;
    }

    /**
     * Check if the effect results to loose action points
     *
     * @param id The effect id
     */
    public static boolean isLooseApEffect(int id) {
        return
            id == 84
            || id == 101
            || id == 168
        ;
    }

    /**
     * Compute the attenuation of an effect based on distance from the center of the area
     *
     * For each unit of distance, the effect is attenuated by 10% (from the previous distance),
     * so this gives the following results :
     * - Distance 0: 100%
     * - Distance 1: 90%
     * - Distance 2: 81%
     * - Distance 5: 59%
     * - Distance 10: 34%
     *
     * The formula used is : {@code value * 0.9 ** distance}
     *
     * @param value The base effect value
     * @param distance The distance from the center of the area
     *
     * @return The attenuated effect value
     */
    public static @NonNegative int applyDistanceAttenuation(@NonNegative int value, @NonNegative int distance) {
        // Usage of Math.pow is around 5x slower than performing the calculation manually using integer
        // So we use a switch to optimize the most common cases (nearly all classes spells have area <= 4)
        switch (distance) {
            case 0:
                return value;

            case 1:
                return value * 9 / 10;

            case 2:
                return value * 81 / 100;

            case 3:
                return value * 729 / 1000;

            case 4:
                return value * 6561 / 10000;

            default:
                return Asserter.castNonNegative((int) (value * Math.pow(0.9, distance)));
        }
    }
}
