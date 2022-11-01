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
import fr.quatrevieux.araknemu.game.fight.ai.action.util.CastSpell;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Optional;

/**
 * Try to debuff (apply negative buffs) enemies
 */
public final class Debuff<F extends ActiveFighter> implements ActionGenerator<F>, CastSpell.SimulationSelector {
    private final CastSpell<F> generator;

    @SuppressWarnings({"argument", "assignment"})
    public Debuff(Simulator simulator) {
        this.generator = new CastSpell<>(simulator, this);
    }

    @Override
    public void initialize(AI<F> ai) {
        generator.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI<F> ai, AiActionFactory actions) {
        return generator.generate(ai, actions);
    }

    @Override
    public boolean valid(CastSimulation simulation) {
        if (simulation.suicideProbability() > 0 || simulation.killedAllies() > 0 || simulation.enemiesBoost() >= 0) {
            return false;
        }

        return simulation.enemiesBoost() < simulation.alliesBoost() + simulation.selfBoost();
    }

    @Override
    public double score(CastSimulation simulation) {
        final double score =
            - simulation.enemiesBoost()
            + simulation.alliesBoost()
            + simulation.selfBoost()
        ;

        return score / simulation.actionPointsCost();
    }
}
