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

package fr.quatrevieux.araknemu.game.fight.ai.action.util;

/**
 * Helper class for any mathematical formula
 */
public final class Formula {
    private Formula() {
        // Disallow instantiation
    }

    /**
     * Transform score value in interval [-inf; +inf] to bounded value [0; 1]
     *
     * @param value Score to transform
     */
    public static double sigmoid(double value) {
        if (value == Double.NEGATIVE_INFINITY) {
            return 0.0;
        }

        if (value == Double.POSITIVE_INFINITY) {
            return 1.0;
        }

        return 0.5 + value / (2 * (1 + Math.abs(value)));
    }
}
