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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.ReflectedDamage;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Hook action for apply buff effects
 */
public interface BuffHook {
    /**
     * Apply effect when fighter turn is started
     *
     * @return False if the fighter cannot start the turn
     */
    public default boolean onStartTurn(FightBuff buff) {
        return true;
    }

    /**
     * Apply effect on turn ending
     */
    public default void onEndTurn(FightBuff buff, Turn turn) {}

    /**
     * Start the buff
     */
    public default void onBuffStarted(FightBuff buff) {}

    /**
     * The buff is terminated (buff expired, debuff...)
     */
    public default void onBuffTerminated(FightBuff buff) {}

    /**
     * The fighter has cast a spell or close combat attack
     * This buff is called before apply effects and resolve targets using {@link BuffHook#onCastTarget(FightBuff, FightCastScope)}
     */
    public default void onCast(FightBuff buff, FightCastScope cast) {}

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
     * @see CastScope#removeTarget(FighterData)
     * @see CastScope#replaceTarget(FighterData, FighterData)
     */
    public default boolean onCastTarget(FightBuff buff, FightCastScope cast) {
        return true;
    }

    /**
     * The fighter will take damages
     */
    public default void onDamage(FightBuff buff, Damage value) {}

    /**
     * The fighter will take damages
     */
    public default void onDirectDamage(FightBuff buff, Fighter caster, Damage value) {
        onDamage(buff, value);
    }

    /**
     * Direct damage has been applied to the current fighter
     * This hook is called after {@link BuffHook#onDirectDamage(FightBuff, Fighter, Damage)}
     *
     * Unlike {@link BuffHook#onLifeAltered(FightBuff, int)} this hook is only called by direct damage attack, so it ignores
     * poison, push damage and some special effects.
     *
     * Note: this hook is not called if the attack has killed the fighter
     *
     * @param buff Active buff
     * @param caster Attack caster
     * @param damage Applied damage. Always positive. If an attack is inefficient, this hook will not be called.
     */
    public default void onDirectDamageApplied(FightBuff buff, Fighter caster, @Positive int damage) {}

    /**
     * The fighter will take damages indirectly (like poison)
     */
    public default void onIndirectDamage(FightBuff buff, Fighter caster, Damage value) {
        onDamage(buff, value);
    }

    /**
     * The fighter will take damages by a buff (i.e. poison)
     *
     * @param buff The current buff
     * @param poison The poison buff
     * @param value The damage to apply
     */
    public default void onBuffDamage(FightBuff buff, FightBuff poison, Damage value) {
        onIndirectDamage(buff, buff.caster(), value);
    }

    /**
     * The fighter life has been altered
     *
     * Unlike {@link BuffHook#onDamage(FightBuff, Damage)}, the effects has already been applied
     *
     * @param buff The active buff
     * @param value Altered life value. Negative for a damage, positive for a heal
     *
     * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterLife#damage(Fighter, int)
     * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterLife#heal(Fighter, int)
     */
    public default void onLifeAltered(FightBuff buff, int value) {}

    /**
     * The fighter has suffered a damage, so its life has been altered
     * By default, this method will forward the call to {@link BuffHook#onLifeAltered(FightBuff, int)}
     *
     * This buff is always called when damage is applied, even if the damage is completely absorbed,
     * or in case of direct or indirect damage.
     *
     * Unlike {@link BuffHook#onDamage(FightBuff, Damage)}, the effects has already been applied
     *
     * Note: this hook is not called if the attack has killed the fighter
     *
     * @param buff The active buff
     * @param value Altered life value. Can be 0 when the effect is completely absorbed
     *
     * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterLife#damage(Fighter, int)
     * @see #onDirectDamageApplied(FightBuff, Fighter, int) To hook only damage applied by direct attack
     */
    public default void onDamageApplied(FightBuff buff, @NonNegative int value) {
        onLifeAltered(buff, -value);
    }

    /**
     * Elemental damage has been applied to the current fighter
     *
     * This hook is called after {@link BuffHook#onDamageApplied(FightBuff, int)}, but only in case
     * of damage related to an element (e.i. fire, water, air, earth, neutral)
     *
     * @param buff The active buff
     * @param element The element of the damage
     * @param value The damage value. Can be 0 if the damage is completely absorbed
     */
    public default void onElementDamageApplied(FightBuff buff, Element element, @NonNegative int value) {}

    /**
     * The fighter life has been healed
     * By default, this method will forward the call to {@link BuffHook#onLifeAltered(FightBuff, int)}
     *
     * This hook is called after heal is applied.
     *
     * @param buff The active buff
     * @param value Altered life value. Can be 0 when the fighter is already full life, so heal has no effect
     *
     * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterLife#heal(Fighter, int)
     */
    public default void onHealApplied(FightBuff buff, @NonNegative int value) {
        onLifeAltered(buff, value);
    }

    /**
     * Damage has been reflected by the cast target
     *
     * The target can be changed using {@link ReflectedDamage#changeTarget(Fighter)}
     * Or modified using {@link ReflectedDamage#multiply(int)}
     *
     * @param buff The active buff
     * @param damage The reflected damage
     */
    public default void onReflectedDamage(FightBuff buff, ReflectedDamage damage) {}

    /**
     * A damage will be applied on target
     * Use this hook to configure damage before apply it
     *
     * Unlike {@link BuffHook#onDamage(FightBuff, Damage)} this hook is called on the caster instead of the target
     *
     * @param buff The active buff
     * @param damage Damage to configure
     * @param target The effect target
     */
    public default void onCastDamage(FightBuff buff, Damage damage, Fighter target) {}

    /**
     * A new effect value is generated by the current fighter
     *
     * Note: {@link BuffHook#onEffectValueTarget(FightBuff, EffectValue)} is always called on the target after
     *
     * @param buff The active buff
     * @param value The new effect value which will be applied
     */
    public default void onEffectValueCast(FightBuff buff, EffectValue value) {}

    /**
     * An effect value will be applied on the current fighter
     *
     * Note: {@link BuffHook#onEffectValueCast(FightBuff, EffectValue)} is always called on the caster before
     *
     * @param buff The active buff
     * @param value The effect value which will be applied
     */
    public default void onEffectValueTarget(FightBuff buff, EffectValue value) {}

    /**
     * A characteristic of the fighter has been altered
     *
     * @param buff The active buff
     * @param characteristic The altered characteristic
     * @param value The characteristic modifier. Positive for add the characteristic, or negative to remove.
     */
    public default void onCharacteristicAltered(FightBuff buff, Characteristic characteristic, int value) {}
}
