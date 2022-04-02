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
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.util.Optional;

/**
 * Try to boost allies (or self)
 *
 * Self boost is priorized to allies boost.
 * The selected spell must, at least, boost allies or self.
 */
public final class Boost implements ActionGenerator, CastSpell.SimulationSelector {
    private final CastSpell generator;
    private final double selfBoostRate;
    private final double alliesBoostRate;
    private final int minDuration;
    private final boolean allowWithoutDelay;

    @SuppressWarnings({"argument", "assignment"})
    public Boost(Simulator simulator, double selfBoostRate, double alliesBoostRate, int minDuration, boolean allowWithoutDelay) {
        this.generator = new CastSpell(simulator, this);

        this.selfBoostRate = selfBoostRate;
        this.alliesBoostRate = alliesBoostRate;
        this.minDuration = minDuration;
        this.allowWithoutDelay = allowWithoutDelay;
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
        // @todo spell filter on interface
        if (!allowWithoutDelay && simulation.spell().constraints().launchDelay() <= 1) {
            return false;
        }

        if (simulation.spell().effects().stream().map(SpellEffect::duration).noneMatch(duration -> duration >= minDuration)) {
            return false;
        }

        if (simulation.suicideProbability() > 0 || simulation.killedAllies() > 0) {
            return false;
        }

        final double totalBoost = simulation.alliesBoost() + simulation.selfBoost();

        return totalBoost > 0 && totalBoost + simulation.alliesLife() + simulation.selfLife() > 0;
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
    private double score(CastSimulation simulation) {
        double score =
            + simulation.alliesBoost() * alliesBoostRate
            + simulation.selfBoost() * selfBoostRate
            - simulation.enemiesBoost()
        ;

        if (simulation.alliesLife() < 0) {
            score += simulation.alliesLife();
        }

        if (simulation.selfLife() < 0) {
            score += simulation.selfLife();
        }

        return score / simulation.spell().apCost();
    }

    /**
     * Configure boost action with prioritization of self boost
     * And allow only long effects (>= 2 turns) to permit usage before {@link Attack}
     */
    public static Boost self(Simulator simulator) {
        return new Boost(simulator, 2d, 1d, 2, false);
    }

    /**
     * Configure boost action with prioritization of allies boost
     * Temporary effects (1 turn) are allowed.
     * This action must be declared after {@link Attack}
     */
    public static Boost allies(Simulator simulator) {
        return new Boost(simulator, 0.5d, 2d, 1, true);
    }
}
