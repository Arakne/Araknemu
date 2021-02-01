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
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Optional;

/**
 * Try to boost allies (or self)
 *
 * Self boost is priorized to allies boost.
 * The selected spell must, at least, boost allies or self.
 */
public final class Boost implements ActionGenerator, CastSpell.SimulationSelector {
    private final CastSpell generator;

    public Boost(Simulator simulator) {
        this.generator = new CastSpell(simulator, this);
    }

    @Override
    public void initialize(AI ai) {
        generator.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI ai) {
        return generator.generate(ai);
    }

    @Override
    public boolean valid(CastSimulation simulation) {
        return simulation.alliesBoost() > 0 || simulation.selfBoost() > 0;
    }

    @Override
    public boolean compare(CastSimulation a, CastSimulation b) {
        return score(a) > score(b);
    }

    /**
     * Compute the score for the given simulation
     *
     * @param simulation The simulation result
     *
     * @return The score of the simulation
     */
    private int score(CastSimulation simulation) {
        final int score =
            + simulation.alliesBoost()
            + simulation.selfBoost() * 2
            - simulation.enemiesBoost()
        ;

        return score / simulation.spell().apCost();
    }
}
