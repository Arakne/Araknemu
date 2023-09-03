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

package fr.quatrevieux.araknemu.game.fight.ai.simulation;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.EffectSimulator;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

import java.util.HashMap;
import java.util.Map;

/**
 * Perform simulation on fight
 */
public final class Simulator {
    private final Map<Integer, EffectSimulator> simulators = new HashMap<>();
    private final CriticalityStrategy criticalityStrategy;

    public Simulator(CriticalityStrategy criticalityStrategy) {
        this.criticalityStrategy = criticalityStrategy;
    }

    /**
     * Register an effect simulator
     *
     * @param effectId The effect to simulate
     * @param simulator The simulator
     */
    public void register(int effectId, EffectSimulator simulator) {
        simulators.put(effectId, simulator);
    }

    /**
     * Simulate the spell cast
     *
     * @param spell Spell to cast
     * @param ai The AI of the current fighter
     * @param caster The caster (current fighter)
     * @param target The cell target
     *
     * @return The simulation result
     */
    public CastSimulation simulate(Spell spell, AI ai, ActiveFighter caster, BattlefieldCell target) {
        final CastSimulation normalSimulation = simulate(spell, ai, new SimulationCastScope(spell, caster, target, spell.effects()));
        final int hitRate = spell.criticalHit();

        if (hitRate < 2) {
            return normalSimulation;
        }

        final CastSimulation criticalSimulation = simulate(spell, ai, new SimulationCastScope(spell, caster, target, spell.criticalEffects()));
        final CastSimulation simulation = new CastSimulation(spell, caster, target);

        final int criticalRate = 100 / criticalityStrategy.hitRate(caster, hitRate);

        simulation.merge(normalSimulation, 100 - criticalRate);
        simulation.merge(criticalSimulation, criticalRate);

        return simulation;
    }

    /**
     * Compute the theoretical score of a spell
     *
     * Unlike {@link #simulate(Spell, AI, ActiveFighter, BattlefieldCell)} no simulation is performed, so this score will
     * not take in account the target resistance, placement, etc...
     *
     * @param spell The spell to score
     * @param characteristics The characteristics of the caster
     *
     * @return The spell score
     */
    public SpellScore score(Spell spell, Characteristics characteristics) {
        final SpellScore score = new SpellScore(spell.constraints().range().max());

        for (SpellEffect effect : spell.effects()) {
            // Ignore probable effects
            if (effect.probability() > 0) {
                continue;
            }

            final EffectSimulator simulator = simulators.get(effect.effect());

            if (simulator != null) {
                simulator.score(score, effect, characteristics);
            }
        }

        return score;
    }

    /**
     * Simulate a cast result
     *
     * @param spell The spell to simulate
     * @param scope The cast scope
     * @param ai The AI of the current fighter
     */
    private CastSimulation simulate(Spell spell, AI ai, CastScope<FighterData, BattlefieldCell> scope) {
        // Remove invisible fighters from simulation
        scope.targets().forEach(target -> {
            if (target.hidden()) {
                scope.removeTarget(target);
            }
        });

        final CastSimulation simulation = new CastSimulation(spell, scope.caster(), scope.target());

        for (CastScope.EffectScope<FighterData, BattlefieldCell> effect : scope.effects()) {
            final EffectSimulator simulator = simulators.get(effect.effect().effect());

            if (simulator == null) {
                continue;
            }

            if (effect.effect().probability() > 0) {
                final CastSimulation probableSimulation = new CastSimulation(spell, scope.caster(), scope.target());

                simulator.simulate(probableSimulation, ai, effect);
                simulation.merge(probableSimulation, effect.effect().probability());
            } else {
                simulator.simulate(simulation, ai, effect);
            }
        }

        return simulation;
    }
}
