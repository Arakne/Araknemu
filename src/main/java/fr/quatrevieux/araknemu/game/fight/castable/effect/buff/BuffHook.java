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

package fr.quatrevieux.araknemu.game.fight.castable.effect.buff;

import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;

/**
 * Hook action for apply buff effects
 */
public interface BuffHook {
    /**
     * Apply effect when fighter turn is started
     *
     * @return False if the fighter cannot start the turn
     */
    public default boolean onStartTurn(Buff buff) {
        return true;
    }

    /**
     * Apply effect on turn ending
     */
    public default void onEndTurn(Buff buff) {}

    /**
     * Start the buff
     */
    public default void onBuffStarted(Buff buff) {}

    /**
     * The buff is terminated (buff expired, debuff...)
     */
    public default void onBuffTerminated(Buff buff) {}

    /**
     * The fighter is a target of a cast
     */
    public default void onCastTarget(Buff buff, CastScope cast) {}

    /**
     * The fighter will take damages
     */
    public default void onDamage(Buff buff, Damage value) {}

    /**
     * The fighter will take damages
     */
    public default void onDirectDamage(Buff buff, ActiveFighter caster, Damage value) {
        onDamage(buff, value);
    }

    /**
     * The fighter will take damages by a buff (i.e. poison)
     *
     * @param buff The current buff
     * @param poison The poison buff
     * @param value The damage to apply
     */
    public default void onBuffDamage(Buff buff, Buff poison, Damage value) {
        onDamage(buff, value);
    }

    /**
     * The fighter life has been altered
     *
     * Unlike {@link BuffHook#onDamage(Buff, Damage)}, the effects has already been applied
     *
     * @param buff The active buff
     * @param value Altered life value. Negative for a damage, positive for a heal
     *
     * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterLife#alter(fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter, int)
     */
    public default void onLifeAltered(Buff buff, int value) {}
}
