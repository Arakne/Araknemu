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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.ai.util.SpellCaster;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.Optional;

/**
 * Try to cast the best spell
 */
final public class CastSpell implements ActionGenerator {
    public interface SimulationSelector {
        /**
         * Check if the simulation is valid
         */
        public boolean valid(CastSimulation simulation);

        /**
         * Compare the two simulation
         * Return true if a is better than b
         *
         * Note: The simulations may be null
         */
        public boolean compare(CastSimulation a, CastSimulation b);
    }

    final private Simulator simulator;
    final private SimulationSelector selector;

    private SpellCaster caster;

    public CastSpell(Simulator simulator, SimulationSelector selector) {
        this.simulator = simulator;
        this.selector = selector;
    }

    @Override
    public void initialize(AI ai) {
        caster = new SpellCaster(ai);
    }

    @Override
    public Optional<Action> generate(AI ai) {
        final int actionPoints = ai.turn().points().actionPoints();

        if (actionPoints < 1 || !ai.enemy().isPresent()) {
            return Optional.empty();
        }

        CastSimulation bestSimulation = null;

        for (Spell spell : ai.fighter().spells()) {
            if (spell.apCost() > actionPoints) {
                continue;
            }

            bestSimulation = bestTargetForSpell(ai, spell, bestSimulation);
        }

        return Optional
            .ofNullable(bestSimulation)
            .map(simulation -> caster.create(simulation.spell(), simulation.target()))
        ;
    }

    private CastSimulation bestTargetForSpell(AI ai, Spell spell, CastSimulation bestSimulation) {
        for (FightCell targetCell : ai.map()) {
            // Target or launch is not valid
            if (!targetCell.walkableIgnoreFighter() || !caster.validate(spell, targetCell)) {
                continue;
            }

            // Simulate spell effects
            final CastSimulation simulation = simulator.simulate(spell, ai.fighter(), targetCell);

            // The spell is not valid for the selector
            if (!selector.valid(simulation)) {
                continue;
            }

            // Select the best simulation
            if (bestSimulation == null || selector.compare(simulation, bestSimulation)) {
                bestSimulation = simulation;
            }
        }

        return bestSimulation;
    }
}
