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

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.SpellScore;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Simulator for simple alter action points.
 * Handle adding or removing actions points on current turn
 */
public final class AlterActionPointsSimulator implements EffectSimulator {
    private final int multiplier;

    /**
     * Creates with defining a multiplier
     *
     * @param multiplier The value multiplier. Can be negative for malus buff
     */
    public AlterActionPointsSimulator(int multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        int duration = effect.effect().duration();

        if (duration == -1 || duration > 10) {
            duration = 10;
        }

        // Get average effect value
        final double value = effect.effect().max() < effect.effect().min()
            ? effect.effect().min()
            : (double) (effect.effect().min() + effect.effect().max()) / 2
        ;

        for (FighterData target : effect.targets()) {
            apply(simulation, target, value, duration);
        }
    }

    /**
     * Apply simulation to one target
     *
     * @param simulation Simulation bag
     * @param target Spell effect target
     * @param value Effect value (i.e. number of points to add or remove)
     * @param duration Effect duration. 0 for effect applied only on current turn.
     */
    private void apply(CastSimulation simulation, FighterData target, double value, @NonNegative int duration) {
        // Modify actions points on current turn
        if (target.equals(simulation.caster())) {
            applyToCurrentFighter(simulation, target, value, duration);
        }

        // Only define as boost if it's a buff effect
        if (duration > 0) {
            simulation.addBoost(value * multiplier * duration, target);
        }
    }

    /**
     * Apply to current fighter to handle actual spell AP cost
     */
    private void applyToCurrentFighter(CastSimulation simulation, FighterData target, double value, @NonNegative int duration) {
        // In case of negative effect compute a boost is not needed : it will automatically take in account by AP cost
        if (multiplier < 0) {
            simulation.alterActionPoints(-value);
            return;
        }

        simulation.alterActionPoints(value);

        // Spell adds more AP than its removes : use added AP as boost
        if (duration == 0 && value > simulation.spell().apCost()) {
            simulation.addBoost((value - simulation.spell().apCost()) * multiplier, target);
        }
    }

    @Override
    public void score(SpellScore score, SpellEffect effect, Characteristics characteristics) {
        final int min = effect.min();
        final int max = Math.max(effect.max(), min);
        final int value = (min + max) * multiplier / 2;

        if (value < 0) {
            score.addDebuff(-value);
        } else {
            score.addBoost(value);
        }
    }
}
