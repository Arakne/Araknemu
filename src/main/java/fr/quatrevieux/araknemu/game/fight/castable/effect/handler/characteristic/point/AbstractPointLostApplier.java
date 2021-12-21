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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.characteristic.point;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectValue;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffEffect;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.player.characteristic.ComputedCharacteristics;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;

/**
 * Compute and apply turn point lost (action or movement)
 *
 * Formula: ([current points] / [base points]) * ([caster wisdom / 10] / [target dodge]) * 1/2
 * Each removal will be considered (and computed) independently : 2 AP loose with one spell is equivalent to two spells with 1 AP loose
 *
 * See: https://forums.jeuxonline.info/sujet/801243/les-formules-de-calcul-dans-dofus#titre_7
 */
public abstract class AbstractPointLostApplier {
    public static final int USE_SPELL_EFFECT = 0;

    private final Fight fight;
    private final AlterPointHook hook;
    private final Characteristic characteristic;
    private final Characteristic resistance;
    private final int removalPointEffect;

    private final RandomUtil random = new RandomUtil();

    protected AbstractPointLostApplier(Fight fight, AlterPointHook hook, Characteristic characteristic, Characteristic resistance, int removalPointEffect) {
        this.fight = fight;
        this.hook = hook;
        this.characteristic = characteristic;
        this.resistance = resistance;
        this.removalPointEffect = removalPointEffect;
    }

    /**
     * Apply the point lost effect
     *
     * A buff will be added and applied if the target do not dodge all points loose
     * If the target dodge a point lost, a message will be sent to the fight
     *
     * @param cast The cast action
     * @param target The fighter target
     * @param effect Effect to apply
     *
     * @return Number of lost points
     */
    public final int apply(CastScope cast, PassiveFighter target, SpellEffect effect)  {
        final ActiveFighter caster = cast.caster();

        final int baseValue = new EffectValue(effect).value();
        final int lost = computePointLost(caster, target, baseValue);
        final int dodge = baseValue - lost;

        if (dodge > 0) {
            fight.send(dodgeMessage(caster, target, dodge));
        }

        if (lost > 0) {
            target.buffs().add(new Buff(buffEffect(effect, lost), cast.action(), caster, target, hook));
        }

        return lost;
    }

    /**
     * The packet to send when the target dodge the point lost
     */
    protected abstract ActionEffect dodgeMessage(PassiveFighter caster, PassiveFighter target, int value);

    /**
     * Create the buff effect for the given point lost
     */
    private SpellEffect buffEffect(SpellEffect baseEffect, int pointLost) {
        return removalPointEffect == USE_SPELL_EFFECT
            ? BuffEffect.fixed(baseEffect, pointLost)
            : BuffEffect.withCustomEffect(baseEffect, removalPointEffect, pointLost)
        ;
    }

    /**
     * Compute how many points will be loose
     *
     * @param caster The spell caster
     * @param target The effect target
     * @param value The base buff value
     *
     * @return Number of lost points. Can be 0 if dodge all loose, and cannot exceed value parameter.
     */
    private int computePointLost(ActiveFighter caster, PassiveFighter target, int value) {
        final int maxPoints = target.characteristics().initial().get(this.characteristic);
        final int currentPoints = target.characteristics().get(this.characteristic);

        // Can't lose points anymore
        if (currentPoints <= 0) {
            return 0;
        }

        // The fighter has only boosted points (ignore division by 0)
        if (maxPoints <= 0) {
            return Math.min(currentPoints, value);
        }

        final int resistance = Math.max(target.characteristics().get(this.resistance), 1);
        final int wisdom = Math.max(caster.characteristics().get(Characteristic.WISDOM) / ComputedCharacteristics.POINT_RESISTANCE_FACTOR, 1);
        final int resistanceRate = 50 * wisdom / resistance;

        int lost = 0;

        // Compute point lost one by one
        for (int i = 0; i < value; ++i) {
            final int afterLost = currentPoints - lost;

            // All points are lost
            if (afterLost <= 0) {
                break;
            }

            int chance = resistanceRate * afterLost / maxPoints;

            // Bound chance between 10% and 90%
            // See: https://web.archive.org/web/20100111082721/http://devblog.dofus.com/fr/billets/46-nouvelle-formule-esquive-equilibrage-xelor.html
            chance = Math.min(chance, 90);
            chance = Math.max(chance, 10);

            if (random.bool(chance)) {
                ++lost;
            }
        }

        return lost;
    }
}
