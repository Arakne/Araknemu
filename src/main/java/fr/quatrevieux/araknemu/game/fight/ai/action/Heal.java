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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.util.CastSpell;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.ActionsFactory;

import java.util.Optional;

/**
 * Try to heal allies
 *
 * Select spells which heal allies or self
 * All cells are tested for select the most effective target for each spells
 */
public final class Heal<F extends ActiveFighter> implements ActionGenerator<F>, CastSpell.SimulationSelector {
    private final CastSpell<F> generator;

    @SuppressWarnings({"assignment", "argument"})
    public Heal(Simulator simulator) {
        this.generator = new CastSpell<>(simulator, this);
    }

    @Override
    public void initialize(AI<F> ai) {
        generator.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI<F> ai, ActionsFactory<F> actions) {
        return generator.generate(ai, actions);
    }

    @Override
    public boolean valid(CastSimulation simulation) {
        if (
            simulation.alliesLife() + simulation.selfLife() <= 0
            || simulation.killedAllies() > 0.1
            || simulation.suicideProbability() > 0.1
        ) {
            return false;
        }

        // Apply more heal on enemies than allies
        return simulation.alliesLife() + simulation.selfLife() > simulation.enemiesLife();
    }

    @Override
    public double score(CastSimulation simulation) {
        final double score = healScore(simulation) + boostScore(simulation);

        return score / simulation.actionPointsCost();
    }

    private double healScore(CastSimulation simulation) {
        return simulation.alliesLife() + simulation.selfLife() - simulation.enemiesLife();
    }

    private double boostScore(CastSimulation simulation) {
        return (simulation.alliesBoost() + simulation.selfBoost() - simulation.enemiesBoost()) / 10;
    }
}
