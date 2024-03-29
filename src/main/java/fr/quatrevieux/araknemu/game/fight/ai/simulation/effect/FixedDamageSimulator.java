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
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.util.Formula;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.GTENegativeOne;

import java.util.Collection;

/**
 * Simulator for fixed damage effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.damage.FixedDamageHandler The simulated effect
 */
public final class FixedDamageSimulator implements EffectSimulator {
    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final SpellEffect spellEffect = effect.effect();
        final Interval damage = damage(spellEffect);

        if (spellEffect.duration() == 0) {
            simulateDamage(simulation, damage, effect.targets());
        } else {
            simulatePoison(simulation, damage, spellEffect.duration(), effect.targets());
        }
    }

    @Override
    public void score(SpellScore score, SpellEffect effect, Characteristics characteristics) {
        score.addDamage(Asserter.castNonNegative((int) damage(effect).average()));
    }

    private Interval damage(SpellEffect effect) {
        return Interval.of(effect.min(), Math.max(effect.max(), effect.min()));
    }

    private void simulatePoison(CastSimulation simulation, Interval damage, @GTENegativeOne int duration, Collection<? extends FighterData> targets) {
        final int capedDuration = Formula.capedDuration(duration);

        for (FighterData target : targets) {
            simulation.addPoison(damage, capedDuration, target);
        }
    }

    private void simulateDamage(CastSimulation simulation, Interval damage, Collection<? extends FighterData> targets) {
        for (FighterData target : targets) {
            simulation.addDamage(damage, target);
        }
    }
}
