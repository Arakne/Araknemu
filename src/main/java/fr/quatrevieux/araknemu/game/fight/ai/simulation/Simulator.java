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

import fr.quatrevieux.araknemu.game.fight.ai.simulation.effect.EffectSimulator;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.Spell;

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
     * @param caster The caster (current fighter)
     * @param target The cell target
     *
     * @return The simulation result
     */
    public CastSimulation simulate(Spell spell, ActiveFighter caster, FightCell target) {
        final CastSimulation normalSimulation = simulate(spell, CastScope.simple(spell, caster, target, spell.effects()));
        final int hitRate = spell.criticalHit();

        if (hitRate < 2) {
            return normalSimulation;
        }

        final CastSimulation criticalSimulation = simulate(spell, CastScope.simple(spell, caster, target, spell.criticalEffects()));
        final CastSimulation simulation = new CastSimulation(spell, caster, target);

        final int criticalRate = 100 / criticalityStrategy.hitRate(caster, hitRate);

        simulation.merge(normalSimulation, 100 - criticalRate);
        simulation.merge(criticalSimulation, criticalRate);

        return simulation;
    }

    /**
     * Simulate a cast result
     *
     * @param scope The cast scope
     */
    private CastSimulation simulate(Spell spell, CastScope scope) {
        // Remove invisible fighters from simulation
        scope.targets().forEach(target -> {
            if (target.hidden()) {
                scope.removeTarget(target);
            }
        });

        final CastSimulation simulation = new CastSimulation(spell, scope.caster(), scope.target());

        for (CastScope.EffectScope effect : scope.effects()) {
            final EffectSimulator simulator = simulators.get(effect.effect().effect());

            if (simulator == null) {
                continue;
            }

            if (effect.effect().probability() > 0) {
                final CastSimulation probableSimulation = new CastSimulation(spell, scope.caster(), scope.target());

                simulator.simulate(probableSimulation, effect);
                simulation.merge(probableSimulation, effect.effect().probability());
            } else {
                simulator.simulate(simulation, effect);
            }
        }

        return simulation;
    }
}
