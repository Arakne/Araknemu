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
        target.buffs().onDirectDamage(caster, damage);

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
        target.buffs().onBuffDamage(buff, damage);

        return applyDamage(caster, damage, target);
    }

    /**
     * Create the damage object
     */
    private Damage computeDamage(ActiveFighter caster, SpellEffect effect, PassiveFighter target) {
        final EffectValue value = new EffectValue(effect)
            .percent(caster.characteristics().get(element.boost()))
            .percent(caster.characteristics().get(Characteristic.PERCENT_DAMAGE))
            .fixed(caster.characteristics().get(Characteristic.FIXED_DAMAGE))
        ;

        return new Damage(value.value(), element)
            .percent(target.characteristics().get(element.percentResistance()))
            .fixed(target.characteristics().get(element.fixedResistance()))
        ;
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

        // @todo returned damage

        return target.life().alter(caster, -damage.value());
    }
}
