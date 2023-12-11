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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.util.Formula;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Simulate damage per AP used effect
 *
 * To compute an approximate damage, it will consider as fire poison of 80% of the target AP multiplied
 * by the damage per AP
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.DamageOnActionPointUseHandler
 */
public final class DamageOnActionPointUseSimulator implements EffectSimulator {
    private static final Element ELEMENT = Element.FIRE;
    private static final double AP_FACTOR = 0.8;

    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final FighterData caster = simulation.caster();
        final SpellEffect spellEffect = effect.effect();

        final int boost = caster.characteristics().get(ELEMENT.boost());
        final int percent = caster.characteristics().get(Characteristic.PERCENT_DAMAGE);
        final int fixed = caster.characteristics().get(Characteristic.FIXED_DAMAGE);

        final double damagePerAp = ((double) spellEffect.max() / spellEffect.min()) * (1 + boost / 100d) * (1 + percent / 100d);
        final int duration = Formula.capedDuration(spellEffect.duration());

        for (FighterData target : effect.targets()) {
            final double ap = target.characteristics().get(Characteristic.ACTION_POINT) * AP_FACTOR;

            simulation.addPoison(
                Interval.of(computeDamage(Asserter.castNonNegative((int) (damagePerAp * ap + fixed)), target)),
                duration,
                target
            );
        }
    }

    private @NonNegative int computeDamage(@NonNegative int value, FighterData target) {
        final Damage damage = new Damage(value, ELEMENT)
            .percent(target.characteristics().get(ELEMENT.percentResistance()))
            .fixed(target.characteristics().get(ELEMENT.fixedResistance()))
        ;

        return Asserter.castNonNegative(damage.value());
    }
}
