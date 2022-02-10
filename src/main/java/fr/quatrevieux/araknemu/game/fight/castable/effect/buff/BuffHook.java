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
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.ReflectedDamage;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;

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
     *
     * To get the spell caster, use {@code cast.caster()}, and to get the target, use {@code buff.target()}.
     *
     * If the target is changed or removed, this method must return false.
     * Retuning false permit to notify that the spell targets has changed to ensure that new
     * target will also be taken in account, but also stop applying other "onCastTarget" hooks
     * on the current fighter.
     *
     * This method will be called on direct and indirect (like spell returned) targets.
     *
     * Implementation:
     * <pre>{@code
     * class MyEffectHandler implements EffectHandler, BuffHook {
     *     public void handle(CastScope cast, CastScope.EffectScope effect) {
     *         // ...
     *     }
     *
     *     // Add the buff to targets
     *     public void buff(CastScope cast, CastScope.EffectScope effect) {
     *         for (PassiveFighter target : effect.targets()) {
     *             target.buffs().add(new Buff(effect.effect(), cast.action(), cast.caster(), target, this));
     *         }
     *     }
     *
     *     // Implements the buff hook
     *     public void onCastTarget(Buff buff, CastScope cast) {
     *         if (!checkCast(cast)) {
     *             return true; // ignore the hook for this cast : return true to continue on this target
     *         }
     *
     *         // Apply buff effect...
     *         // ...
     *
     *         // Change the target
     *         // buff.target() is cast target / fighter who has the given buff applied
     *         cast.replaceTarget(buff.target(), getNewTarget(cast));
     *
     *         // You can also remove the current fighter from spell targets
     *         cast.removeTarget(buff.target());
     *
     *         return false; // The target has been changed (or removed)
     *     }
     * }
     * }</pre>
     *
     * @return true to continue, or false if the cast target has changed (removed or replaced)
     *
     * @see CastScope#removeTarget(PassiveFighter)
     * @see CastScope#replaceTarget(PassiveFighter, PassiveFighter)
     */
    public default boolean onCastTarget(Buff buff, CastScope cast) {
        return true;
    }

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
     * The fighter will take damages indirectly (like poison)
     */
    public default void onIndirectDamage(Buff buff, ActiveFighter caster, Damage value) {
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
        onIndirectDamage(buff, buff.caster(), value);
    }

    /**
     * The fighter life has been altered
     *
     * Unlike {@link BuffHook#onDamage(Buff, Damage)}, the effects has already been applied
     *
     * @param buff The active buff
     * @param value Altered life value. Negative for a damage, positive for a heal
     *
     * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterLife#alter(fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter, int)
     */
    public default void onLifeAltered(Buff buff, int value) {}

    /**
     * Damage has been reflected by the cast target
     *
     * The target can be changed using {@link ReflectedDamage#changeTarget(PassiveFighter)}
     * Or modified using {@link ReflectedDamage#multiply(int)}
     *
     * @param buff The active buff
     * @param damage The reflected damage
     */
    public default void onReflectedDamage(Buff buff, ReflectedDamage damage) {}

    /**
     * A damage will be applied on target
     * Use this hook to configure damage before apply it
     *
     * Unlike {@link BuffHook#onDamage(Buff, Damage)} this hook is called on the caster instead of the target
     *
     * @param buff The active buff
     * @param damage Damage to configure
     * @param target The effect target
     */
    public default void onCastDamage(Buff buff, Damage damage, PassiveFighter target) {}

    /**
     * A new effect value is generated by the current fighter
     *
     * Note: {@link BuffHook#onEffectValueTarget(Buff, EffectValue, PassiveFighter)} is always called on the target after
     *
     * @param buff The active buff
     * @param value The new effect value which will be applied
     */
    public default void onEffectValueCast(Buff buff, EffectValue value) {}

    /**
     * An effect value will be applied on the current fighter
     *
     * Note: {@link BuffHook#onEffectValueCast(Buff, EffectValue)} is always called on the caster before
     *
     * @param buff The active buff
     * @param value The effect value which will be applied
     * @param caster The effect caster
     */
    public default void onEffectValueTarget(Buff buff, EffectValue value, PassiveFighter caster) {}
}
