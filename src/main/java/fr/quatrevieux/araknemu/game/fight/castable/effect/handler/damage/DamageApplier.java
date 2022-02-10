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
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

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
     * Apply a direct damage effect to a fighter
     *
     * Note: do not use this method for a buff, it will call the invalid buff hook
     *
     * @param caster The spell caster
     * @param effect The effect to apply
     * @param target The target
     *
     * @return The real damage value
     *
     * @see DamageApplier#apply(Buff) For apply a buff damage (i.e. poison)
     * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onDirectDamage(ActiveFighter, Damage) The called buff hook
     */
    public int apply(ActiveFighter caster, SpellEffect effect, PassiveFighter target) {
        final Damage damage = computeDamage(caster, effect, target);

        return applyDirectDamage(caster, damage, target);
    }

    /**
     * Apply a fixed (i.e. precomputed) amount of damage on the target
     *
     * Like {@link DamageApplier#apply(ActiveFighter, SpellEffect, PassiveFighter)} :
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
     * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onDirectDamage(ActiveFighter, Damage) The called buff hook
     */
    public int applyFixed(ActiveFighter caster, int value, PassiveFighter target) {
        final Damage damage = createDamage(caster, target, value);

        return applyDirectDamage(caster, damage, target);
    }

    /**
     * Apply a fixed (i.e. precomputed) amount of damage on the target, but as indirect
     *
     * So, unlike {@link DamageApplier#applyFixed(ActiveFighter, int, PassiveFighter)} :
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
     * @see fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onIndirectDamage(ActiveFighter, Damage) The called buff hook
     */
    public int applyIndirectFixed(ActiveFighter caster, int value, PassiveFighter target) {
        final Damage damage = new Damage(value, element)
            .percent(target.characteristics().get(element.percentResistance()))
            .fixed(target.characteristics().get(element.fixedResistance()))
        ;

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
        final PassiveFighter target = buff.target();
        final ActiveFighter caster = buff.caster();

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
    public int applyFixed(Buff buff, int value) {
        final PassiveFighter target = buff.target();
        final Damage damage = createDamage(buff.caster(), target, value);

        return applyBuffDamage(buff, damage, target);
    }

    /**
     * Create the damage object
     */
    private Damage computeDamage(ActiveFighter caster, SpellEffect effect, PassiveFighter target) {
        final EffectValue value = EffectValue.create(effect, caster, target)
            .percent(caster.characteristics().get(element.boost()))
            .percent(caster.characteristics().get(Characteristic.PERCENT_DAMAGE))
            .fixed(caster.characteristics().get(Characteristic.FIXED_DAMAGE))
        ;

        return createDamage(caster, target, value.value());
    }

    /**
     * Apply damage to the target for direct damage
     *
     * This method will call direct damage buff and apply returned damage
     *
     * @return The life change value. Negative for damage, positive for heal.
     */
    private int applyDirectDamage(ActiveFighter caster, Damage damage, PassiveFighter target) {
        target.buffs().onDirectDamage(caster, damage);

        if (!caster.equals(target)) {
            damage.reflect(target.characteristics().get(Characteristic.COUNTER_DAMAGE));
        }

        return applyDamage(caster, damage, target);
    }

    /**
     * Apply damage to the target for buff damage
     *
     * This method will call buff damage buff
     *
     * @return The life change value. Negative for damage, positive for heal.
     */
    private int applyBuffDamage(Buff buff, Damage damage, PassiveFighter target) {
        target.buffs().onBuffDamage(buff, damage);

        return applyDamage(buff.caster(), damage, target);
    }

    /**
     * Apply the damage object to the target
     *
     * @return The life change value. Negative for damage, positive for heal.
     */
    private int applyDamage(ActiveFighter caster, Damage damage, PassiveFighter target) {
        if (damage.reducedDamage() > 0) {
            fight.send(ActionEffect.reducedDamage(target, damage.reducedDamage()));
        }

        final int lifeChange = target.life().alter(caster, -damage.value());

        if (lifeChange < 0 && !target.equals(caster) && damage.reflectedDamage() > 0) {
            applyReflectedDamage(target, caster, damage);
        }

        return lifeChange;
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
    private void applyReflectedDamage(PassiveFighter castTarget, ActiveFighter caster, Damage damage) {
        final ReflectedDamage returnedDamage = new ReflectedDamage(damage, caster);
        caster.buffs().onReflectedDamage(returnedDamage);

        if (returnedDamage.baseValue() > 0) {
            fight.send(ActionEffect.reflectedDamage(castTarget, returnedDamage.baseValue()));
            returnedDamage.target().life().alter(castTarget, -returnedDamage.value());
        }
    }

    /**
     * Create the damage object, and call {@link fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs#onCastDamage(Damage, PassiveFighter)}
     *
     * @param caster The spell caster
     * @param target The effect target
     * @param value Raw damage value
     *
     * @return The damage object to apply
     */
    private Damage createDamage(ActiveFighter caster, PassiveFighter target, int value) {
        final Damage damage = new Damage(value, element)
            .percent(target.characteristics().get(element.percentResistance()))
            .fixed(target.characteristics().get(element.fixedResistance()))
        ;

        caster.buffs().onCastDamage(damage, target);

        return damage;
    }
}
