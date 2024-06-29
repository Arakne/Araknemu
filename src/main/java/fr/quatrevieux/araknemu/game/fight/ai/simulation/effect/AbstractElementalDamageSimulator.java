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
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.util.Formula;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.Element;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.Damage;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.Collection;

/**
 * Base class for simulator of damage related to an element.
 *
 * Those damage can be reduced by armor and resistances.
 * Armor buffs are called when the damage is applied directly to the target (i.e. not a poison effect).
 */
public abstract class AbstractElementalDamageSimulator implements EffectSimulator {
    private final Simulator simulator;
    private final Element element;

    public AbstractElementalDamageSimulator(Simulator simulator, Element element) {
        this.simulator = simulator;
        this.element = element;
    }

    @Override
    public final void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final SpellEffect spellEffect = effect.effect();
        final Interval baseDamage = computeBaseDamage(ai.fighter(), spellEffect);

        if (spellEffect.duration() == 0) {
            simulateDamage(simulation, baseDamage, effect.targets());
        } else {
            simulatePoison(simulation, baseDamage, spellEffect.duration(), effect.targets());
        }
    }

    /**
     * Return the interval of base damage.
     * Base damage are computed with all boosts, but without resistances or armor.
     *
     * @param caster The spell caster
     * @param effect The spell effect
     *
     * @return The interval of base damage
     */
    protected abstract Interval computeBaseDamage(FighterData caster, SpellEffect effect);

    private Interval applyResistances(Interval baseDamageInterval, FighterData target) {
        return baseDamageInterval.map(value -> Asserter.castNonNegative(createDamage(value, target).value()));
    }

    private Interval applyResistancesAndArmor(Interval baseDamageInterval, CastSimulation simulation, FighterData target) {
        return baseDamageInterval.map(value -> Asserter.castNonNegative(createDamageWithArmor(value, simulation, target).value()));
    }

    private Damage createDamage(@NonNegative int baseDamage, FighterData target) {
        return new Damage(baseDamage, element)
            .percent(target.characteristics().get(element.percentResistance()))
            .fixed(target.characteristics().get(element.fixedResistance()))
        ;
    }

    private Damage createDamageWithArmor(@NonNegative int baseDamage, CastSimulation simulation, FighterData target) {
        final Damage damage = simulator.applyReduceableDamageBuffs(target, createDamage(baseDamage, target));
        final int reflectedDamage = damage.reflectedDamage() + target.characteristics().get(Characteristic.COUNTER_DAMAGE);

        if (reflectedDamage > 0) {
            simulation.addDamage(Interval.of(reflectedDamage), simulation.caster());
        }

        return damage;
    }

    private void simulatePoison(CastSimulation simulation, Interval damage, @GTENegativeOne int duration, Collection<? extends FighterData> targets) {
        final int capedDuration = Formula.capedDuration(duration);

        for (FighterData target : targets) {
            simulation.addPoison(applyResistances(damage, target), capedDuration, target);
        }
    }

    private void simulateDamage(CastSimulation simulation, Interval damage, Collection<? extends FighterData> targets) {
        for (FighterData target : targets) {
            simulation.addDamage(applyResistancesAndArmor(damage, simulation, target), target);
        }
    }
}
