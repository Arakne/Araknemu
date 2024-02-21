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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

/**
 * Base type for damage object which can be multiplied
 */
public interface MultipliableDamage {
    /**
     * Compute the final damage value, with multiplier applied
     *
     * @return The damage value. If negative, the damage is transformed to heal.
     *     If positive the number of life point to remove
     *
     * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterLife#damage(Fighter, int)
     * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterLife#heal(Fighter, int)
     */
    public int value();

    /**
     * Multiply the applied damage
     *
     * @param factor The multiplication factor. Can be negative for change damage to heal
     *
     * @return The current instance
     */
    public MultipliableDamage multiply(int factor);
}
