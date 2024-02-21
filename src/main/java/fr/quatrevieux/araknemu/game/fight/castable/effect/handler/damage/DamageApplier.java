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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Apply simple damage to fighter
 *
 * Returns the effect damage value.
 * When applies damage, a negative value will be returned (-50 => The target lose 50 LP)
 * When no effect, zero will be returned
 * When damage is transformed to heal, will return a positive value (50 => The target win 50 LP)
 */
public final class DamageApplier {
    private final Element element;
    private final Fight fight;

    public DamageApplier(Element element, Fight fight) {
        this.element = element;
        this.fight = fight;
    }

    /**
     * Apply a direct damage effect to a fighter, using pre-roll value
     *
     * The pre-roll value should be created using {@link EffectValue#create(SpellEffect, FighterData, FighterData)},
     * or {@link EffectValue#forEachTargets(SpellEffect, FighterData, Iterable, java.util.function.BiConsumer)}
     * with same caster and target as this method.
     *
     * Note: do not use this method for a buff, it will call the invalid buff hook
     *
     * @param caster The spell caster
     * @param effect The effect to apply
     * @param target The target
     * @param value The pre-roll value. Must be configured for the given caster and target.
     * @param distance The distance between the center of the effect and the current target. Should be 0 for single cell effect.
     *
     * @return The real damage value
     *
     * @see DamageApplier#apply(Buff) For apply a buff damage (i.e. poison)
     * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onDirectDamage(Fighter, Damage) The called buff hook
     * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onDirectDamageApplied(Fighter, int) Buff called when damage are applied
     */
    public int apply(Fighter caster, SpellEffect effect, Fighter target, EffectValue value, @NonNegative int distance) {
        final Damage damage = computeDamage(caster, effect, target, value);

        damage.distance(distance);

        return applyDirectDamage(caster, damage, target);
    }

    /**
     * Apply a fixed (i.e. precomputed) amount of damage on the target
     *
     * Like {@link DamageApplier#apply(Fighter, SpellEffect, Fighter, EffectValue, int)} :
     * - resistance are applied
     * - direct damage buff are called
     * - returned damage are applied
     *
     * @param caster The spell caster
     * @param value The damage value. Must be positive. Can be 0 for no damage.
     * @param target The cast target
     *
     * @return The applied damage value. Negative for damage, or positive for heal.
     *
     * @see DamageApplier#applyFixed(Buff, int) For apply a precomputed damage buff
     * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onDirectDamage(Fighter, Damage) The called buff hook
     * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onDirectDamageApplied(Fighter, int) Buff called when damage are applied
     */
    public int applyFixed(Fighter caster, @NonNegative int value, Fighter target) {
        final Damage damage = createDamage(caster, target, value);

        return applyDirectDamage(caster, damage, target);
    }

    /**
     * Apply a fixed (i.e. precomputed) amount of damage on the target, but as indirect
     *
     * So, unlike {@link DamageApplier#applyFixed(Fighter, int, Fighter)} :
     * - indirect damage buff are called instead of direct one
     * - returned damage are not applied
     *
     * But resistance are applied
     *
     * @param caster The spell caster
     * @param value The damage value. Must be positive. Can be 0 for no damage.
     * @param target The cast target
     *
     * @return The applied damage value. Negative for damage, or positive for heal.
     *
     * @see DamageApplier#applyFixed(Buff, int) Apply damage with the same way
     * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onIndirectDamage(Fighter, Damage) The called buff hook
     */
    public int applyIndirectFixed(Fighter caster, @NonNegative int value, Fighter target) {
        final Damage damage = createDamage(caster, target, value);

        target.buffs().onIndirectDamage(caster, damage);

        return applyDamage(caster, damage, target);
    }

    /**
     * Apply a damage buff effect
     *
     * @param buff Buff to apply
     *
     * @return The real damage value
     *
     * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onBuffDamage(Buff, Damage) The called buff hook
     */
    public int apply(Buff buff) {
        final Fighter target = buff.target();
        final Fighter caster = buff.caster();

        final Damage damage = computeDamage(caster, buff.effect(), target);

        return applyBuffDamage(buff, damage, target);
    }

    /**
     * Apply a fixed (i.e. precomputed) amount of damage on the target from a buff
     *
     * Like {@link DamageApplier#apply(Buff)} :
     * - resistance are applied
     * - buff damage buff are called
     *
     * @param buff Buff to apply
     * @param value The damage value. Must be positive. Can be 0 for no damage.
     *
     * @return The applied damage value. Negative for damage, or positive for heal.
     *
     * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onBuffDamage(Buff, Damage) The called buff hook
     */
    public int applyFixed(Buff buff, @NonNegative int value) {
        final Fighter target = buff.target();
        final Damage damage = createDamage(buff.caster(), target, value);

        return applyBuffDamage(buff, damage, target);
    }

    /**
     * Create the damage object
     */
    private Damage computeDamage(Fighter caster, SpellEffect effect, Fighter target) {
        return computeDamage(caster, effect, target, EffectValue.create(effect, caster, target));
    }

    /**
     * Create the damage object, using pre-roll value
     */
    private Damage computeDamage(Fighter caster, SpellEffect effect, Fighter target, EffectValue value) {
        value
            .percent(caster.characteristics().get(element.boost()))
            .percent(caster.characteristics().get(Characteristic.PERCENT_DAMAGE))
            .fixed(caster.characteristics().get(Characteristic.FIXED_DAMAGE))
        ;

        if (element.physical()) {
            value.fixed(caster.characteristics().get(Characteristic.PHYSICAL_DAMAGE));
        }

        if (effect.trap()) {
            value
                .fixed(caster.characteristics().get(Characteristic.TRAP_BOOST))
                .percent(caster.characteristics().get(Characteristic.PERCENT_TRAP_BOOST))
            ;
        }

        return createDamage(caster, target, value.value());
    }

    /**
     * Apply damage to the target for direct damage
     *
     * This method will call direct damage buffs and apply returned damage
     *
     * @return The life change value. Negative for damage, positive for heal.
     */
    private int applyDirectDamage(Fighter caster, Damage damage, Fighter target) {
        target.buffs().onDirectDamage(caster, damage);

        if (!caster.equals(target)) {
            damage.reflect(Math.max(target.characteristics().get(Characteristic.COUNTER_DAMAGE), 0));
        }

        final int actualDamage = applyDamage(caster, damage, target);

        if (actualDamage < 0 && !target.dead()) {
            target.buffs().onDirectDamageApplied(caster, Asserter.castPositive(-actualDamage));
        }

        return actualDamage;
    }

    /**
     * Apply damage to the target for buff damage
     *
     * This method will call buff damage buff
     *
     * @return The life change value. Negative for damage, positive for heal.
     */
    private int applyBuffDamage(Buff buff, Damage damage, Fighter target) {
        target.buffs().onBuffDamage(buff, damage);

        return applyDamage(buff.caster(), damage, target);
    }

    /**
     * Apply the damage object to the target
     *
     * @return The life change value. Negative for damage, positive for heal.
     */
    private int applyDamage(Fighter caster, Damage damage, Fighter target) {
        if (damage.reducedDamage() > 0) {
            fight.send(ActionEffect.reducedDamage(target, damage.reducedDamage()));
        }

        final int damageValue = damage.value();

        // Damage has been transformed to heal
        if (damageValue < 0) {
            return target.life().heal(caster, Asserter.castNonNegative(-damageValue));
        }

        final int actualDamage = target.life().damage(caster, damageValue, damage.baseDamage());

        if (actualDamage > 0 && !target.equals(caster) && damage.reflectedDamage() > 0) {
            applyReflectedDamage(target, caster, damage);
        }

        return -actualDamage;
    }

    /**
     * Apply returned damage on the original caster
     *
     * Notes:
     * - do not handle target change chain
     * - use resistance on returned damage ?
     *
     * @param caster The original spell caster
     * @param damage The applied damage
     */
    private void applyReflectedDamage(Fighter castTarget, Fighter caster, Damage damage) {
        final ReflectedDamage returnedDamage = new ReflectedDamage(damage, caster);
        caster.buffs().onReflectedDamage(returnedDamage);

        if (returnedDamage.baseValue() > 0) {
            fight.send(ActionEffect.reflectedDamage(castTarget, returnedDamage.baseValue()));

            final int actualReturnedDamage = returnedDamage.value();

            if (actualReturnedDamage > 0) {
                returnedDamage.target().life().damage(castTarget, actualReturnedDamage);
            } else {
                returnedDamage.target().life().heal(castTarget, Asserter.castNonNegative(-actualReturnedDamage));
            }
        }
    }

    /**
     * Create the damage object, and call {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onCastDamage(Damage, Fighter)}
     *
     * @param caster The spell caster
     * @param target The effect target
     * @param value Raw damage value
     *
     * @return The damage object to apply
     */
    private Damage createDamage(Fighter caster, Fighter target, @NonNegative int value) {
        final Damage damage = new Damage(value, element)
            .percent(target.characteristics().get(element.percentResistance()))
            .fixed(target.characteristics().get(element.fixedResistance()))
        ;

        caster.buffs().onCastDamage(damage, target);

        return damage;
    }
}
