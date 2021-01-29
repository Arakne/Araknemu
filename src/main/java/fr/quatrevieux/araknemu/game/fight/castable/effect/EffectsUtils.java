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

/**
 * Utility class for effects
 *
 * @todo Should be removed or refactored with Effect enum
 */
final public class EffectsUtils {
    /**
     * Disable constructor
     */
    private EffectsUtils() {}

    /**
     * Check if the effect is a damage effect
     *
     * @param id The effect id
     */
    static public boolean isDamageEffect(int id) {
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
    static public boolean isLooseApEffect(int id) {
        return
            id == 84
            || id == 101
            || id == 168
        ;
    }
}
