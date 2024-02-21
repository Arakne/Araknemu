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
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Handle the fighter life
 */
public interface FighterLife extends Life {
    /**
     * Check if the fighter is dead
     */
    public boolean dead();

    /**
     * Heal the fighter (i.e. add life points)
     *
     * If the fighter is dead, or is full life, this method will do nothing
     * If the value is higher that the current life left, the life will be set to the max life
     *
     * This method will trigger buffs {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onLifeAltered(int)}
     *
     * @param caster The caster of the heal effect
     * @param value The heal value
     *
     * @return The applied heal value. Will be clamped to the left life points.
     */
    public @NonNegative int heal(Fighter caster, @NonNegative int value);

    /**
     * Apply damage to the fighter (i.e. remove life points)
     *
     * If the fighter is dead, this method will do nothing
     * If the value is higher that the current life, the fighter will be considered as dead, and its life will be set to 0
     *
     * This method will trigger buffs {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onLifeAltered(int)}
     *
     * @param caster The caster of the damage effect
     * @param value The damage value
     *
     * @return The actual number of life points removed. If damage are higher than the current life, the returned value will be the remaining life points before the fighter die.
     *
     * @see #damage(Fighter, int, int) To specify the base damage value. On this method, the base damage is equal to the damage value.
     */
    public default @NonNegative int damage(Fighter caster, @NonNegative int value) {
        return damage(caster, value, value);
    }

    /**
     * Apply damage to the fighter (i.e. remove life points)
     *
     * If the fighter is dead, this method will do nothing
     * If the value is higher that the current life, the fighter will be considered as dead, and its life will be set to 0
     *
     * This method will trigger buffs {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onLifeAltered(int)}
     *
     * @param caster The caster of the damage effect
     * @param value The damage value
     * @param baseDamage The base damage value, before applying any reduction. This value is used to compute erosion.
     *
     * @return The actual number of life points removed. If damage are higher than the current life, the returned value will be the remaining life points before the fighter die.
     *
     * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage#baseDamage() To get the base damage parameter
     */
    public @NonNegative int damage(Fighter caster, @NonNegative int value, @NonNegative int baseDamage);

    /**
     * Modify the erosion rate of the fighter
     * This value is added to the current erosion rate, and clamped to [0, 100]
     *
     * @param value The value to add to the current erosion rate. This value is a percentage, so 100 means 100% of the damage will be applied as erosion
     */
    public void alterErosion(int value);

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
    public void alterMax(Fighter caster, int value);

    /**
     * Kill the fighter
     */
    public void kill(Fighter caster);

    /**
     * Resuscitate the fighter
     *
     * If the fighter is already alive, this method will do nothing
     *
     * @param caster The fighter who try to resuscitate the current one
     * @param value The new life points value. This value will be bounded to the max life points.
     */
    public void resuscitate(Fighter caster, @Positive int value);
}
