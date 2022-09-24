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

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.world.creature.Life;

/**
 * Handle the fighter life
 */
public interface FighterLife extends Life {
    /**
     * Check if the fighter is dead
     */
    public boolean dead();

    /**
     * Change fighter life
     *
     * This method will trigger buffs {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onLifeAltered(int)}
     *
     * @param caster The caster
     * @param value The modified value. Positive for heal, negative for damage
     */
    public int alter(PassiveFighter caster, int value);

    /**
     * Change the max life of the current fighter
     * The current life will also be updated
     *
     * The `value` is simply added to `current` and `max` life point values
     * If the current life reach 0, the fighter will be considered as dead
     *
     * @param caster The caster
     * @param value Value to add to current and max. Can be negative for remove life.
     *
     * @see fr.quatrevieux.araknemu.game.fight.fighter.event.FighterMaxLifeChanged Event triggered by this method
     */
    public void alterMax(PassiveFighter caster, int value);

    /**
     * Kill the fighter
     */
    public void kill(PassiveFighter caster);
}
