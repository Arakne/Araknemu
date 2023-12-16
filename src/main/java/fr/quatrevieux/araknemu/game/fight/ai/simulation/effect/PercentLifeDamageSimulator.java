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
import org.checkerframework.checker.index.qual.GTENegativeOne;

import java.util.Collection;

/**
 * Simulator for damage depending on the life of the caster effect
 * Unlike {@link PercentLifeDamageSimulator}, this effect is related to an element, so it can be reduced by armor
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.PercentLifeDamageHandler The simulated effect
 */
public final class PercentLifeDamageSimulator implements EffectSimulator {
    private final Element element;

    public PercentLifeDamageSimulator(Element element) {
        this.element = element;
    }

    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final SpellEffect spellEffect = effect.effect();
        final Interval baseDamage = damage(ai.fighter(), spellEffect);

        if (spellEffect.duration() == 0) {
            simulateDamage(simulation, baseDamage, effect.targets());
        } else {
            simulatePoison(simulation, baseDamage, spellEffect.duration(), effect.targets());
        }
    }

    private Interval damage(FighterData caster, SpellEffect effect) {
        final int currentLife = caster.life().current();

        return Interval.of(effect.min(), Math.max(effect.max(), effect.min()))
            .map(value -> value * currentLife / 100)
        ;
    }

    private Interval applyResistances(Interval damage, FighterData target) {
        return damage.map(value -> Asserter.castNonNegative(new Damage(value, element)
            .percent(target.characteristics().get(element.percentResistance()))
            .fixed(target.characteristics().get(element.fixedResistance()))
            .value()
        ));
    }

    private void simulatePoison(CastSimulation simulation, Interval damage, @GTENegativeOne int duration, Collection<? extends FighterData> targets) {
        final int capedDuration = Formula.capedDuration(duration);

        for (FighterData target : targets) {
            simulation.addPoison(applyResistances(damage, target), capedDuration, target);
        }
    }

    private void simulateDamage(CastSimulation simulation, Interval damage, Collection<? extends FighterData> targets) {
        for (FighterData target : targets) {
            simulation.addDamage(applyResistances(damage, target), target);
        }
    }
}
