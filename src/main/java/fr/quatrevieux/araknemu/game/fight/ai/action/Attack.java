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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Optional;

/**
 * Try to attack enemies
 *
 * Select spells causing damage on enemies
 * All cells are tested for select the most effective target for each spells
 */
public final class Attack implements ActionGenerator, CastSpell.SimulationSelector {
    private final CastSpell generator;
    private final double suicidePenaltyFactor;

    private double averageEnemyLifePoints = 0;

    public Attack(Simulator simulator) {
        this(simulator, 2);
    }

    public Attack(Simulator simulator, double suicidePenaltyFactor) {
        this.generator = new CastSpell(simulator, this);
        this.suicidePenaltyFactor = suicidePenaltyFactor;
    }

    @Override
    public void initialize(AI ai) {
        generator.initialize(ai);
        averageEnemyLifePoints = ai.enemies().mapToInt(fighter -> fighter.life().max()).average().orElse(0);
    }

    @Override
    public Optional<Action> generate(AI ai) {
        return generator.generate(ai);
    }

    @Override
    public boolean valid(CastSimulation simulation) {
        return simulation.enemiesLife() < 0;
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
     * @return The score of the simulation. 0 is null
     *
     * @todo Handle the boost value
     */
    private double score(CastSimulation simulation) {
        double score =
            - simulation.enemiesLife()
            + simulation.alliesLife()
            + simulation.selfLife() * 2
        ;

        final double killRatio =
            simulation.killedEnemies()
            - 1.5 * simulation.killedAllies()
            - suicidePenaltyFactor * simulation.suicideProbability()
        ;

        score += averageEnemyLifePoints * killRatio;

        return score / simulation.spell().apCost();
    }
}
