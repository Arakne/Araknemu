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

package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Effect handler for an use effect
 */
public interface UseEffectHandler {
    /**
     * Apply the item effect to the caster
     *
     * @param effect The effect to apply
     * @param caster The caster
     */
    public void apply(UseEffect effect, ExplorationPlayer caster);

    /**
     * Apply the item effect to a target player or cell
     *
     * {@link UseEffectHandler#checkTarget(UseEffect, ExplorationPlayer, ExplorationPlayer, int)} must be called before
     *
     * @param effect The effect to apply
     * @param caster The effect caster
     * @param target The effect target (can be null)
     * @param cell The target cell. -1 if no cell is targeted
     */
    public default void applyToTarget(UseEffect effect, ExplorationPlayer caster, @Nullable ExplorationPlayer target, @GTENegativeOne int cell) {}

    /**
     * Check if the effect can be used
     *
     * @param effect The effect to apply
     * @param caster The caster
     *
     * @return True if the effect can be applied or false
     */
    public boolean check(UseEffect effect, ExplorationPlayer caster);

    /**
     * Check if the effect can be used to the target
     *
     * @param effect The effect to apply
     * @param caster The caster
     * @param target The effect target (can be null)
     * @param cell The target cell. -1 if no cell is targeted
     *
     * @return True if the effect can be applied or false
     */
    public boolean checkTarget(UseEffect effect, ExplorationPlayer caster, @Nullable ExplorationPlayer target, @GTENegativeOne int cell);

    /**
     * Check if the effect can be used by a fighter during placement
     *
     * @param effect The effect to apply
     * @param fighter The fighter to check
     *
     * @return True if the effect can be applied or false
     */
    public default boolean checkFighter(UseEffect effect, PlayerFighter fighter) {
        return false;
    }

    /**
     * Apply the item effect to the fighter during placement
     *
     * @param effect The effect to apply
     * @param fighter The fighter
     */
    public default void applyToFighter(UseEffect effect, PlayerFighter fighter) {}
}
