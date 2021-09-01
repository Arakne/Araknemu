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

import java.util.Optional;

/**
 * Try to attack enemies
 *
 * Select spells causing damage on enemies
 * All cells are tested for select the most effective target for each spells
 */
public final class Attack implements ActionGenerator, CastSpell.SimulationSelector {
    private final CastSpell generator;
    private final SuicideStrategy suicideStrategy;

    private double averageEnemyLifePoints = 0;
    private int enemiesCount = 0;

    public Attack(Simulator simulator) {
        this(simulator, SuicideStrategy.IF_KILL_ENEMY);
    }

    public Attack(Simulator simulator, SuicideStrategy suicideStrategy) {
        this.generator = new CastSpell(simulator, this);
        this.suicideStrategy = suicideStrategy;
    }

    @Override
    public void initialize(AI ai) {
        generator.initialize(ai);
        averageEnemyLifePoints = ai.helper().enemies().stream().mapToInt(fighter -> fighter.life().max()).average().orElse(0);
        enemiesCount = ai.helper().enemies().count();
    }

    @Override
    public Optional<Action> generate(AI ai) {
        return generator.generate(ai);
    }

    @Override
    public boolean valid(CastSimulation simulation) {
        if (simulation.enemiesLife() >= 0) {
            return false;
        }

        // Kill all enemies
        if (simulation.killedEnemies() >= enemiesCount) {
            return true;
        }

        if (!suicideStrategy.allow(simulation)) {
            return false;
        }

        // Kill more allies than enemies
        if (simulation.killedAllies() > simulation.killedEnemies()) {
            return false;
        }

        // At least one enemy will be killed
        if (simulation.killedEnemies() >= 0.99) {
            return true;
        }

        // Cause more damage on enemies than allies
        return simulation.enemiesLife() < simulation.alliesLife() + simulation.selfLife();
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
     */
    public double score(CastSimulation simulation) {
        final double score = damageScore(simulation) + killScore(simulation) + boostScore(simulation);

        return score / simulation.spell().apCost();
    }

    private double damageScore(CastSimulation simulation) {
        return - simulation.enemiesLife() + simulation.alliesLife() + simulation.selfLife() * 2;
    }

    private double killScore(CastSimulation simulation) {
        final double killRatio = simulation.killedEnemies()
            - 1.5 * simulation.killedAllies()
            - 2 * simulation.suicideProbability()
        ;

        return averageEnemyLifePoints * killRatio;
    }

    private double boostScore(CastSimulation simulation) {
        return (simulation.alliesBoost() + simulation.selfBoost() - simulation.enemiesBoost()) / 10;
    }

    /**
     * Filter the cast by the suicide probability
     *
     * @see CastSimulation#suicideProbability()
     */
    enum SuicideStrategy {
        /**
         * Always allow suicide
         * Should be used on The Sacrificial Doll AI
         */
        ALLOW {
            @Override
            public boolean allow(CastSimulation simulation) {
                return true;
            }
        },

        /**
         * Suicide is never accepted
         */
        DENY {
            @Override
            public boolean allow(CastSimulation simulation) {
                return simulation.suicideProbability() <= 0;
            }
        },

        /**
         * Suicide is accepted only if there is more chance (or number) to kill an enemy
         */
        IF_KILL_ENEMY {
            @Override
            public boolean allow(CastSimulation simulation) {
                return simulation.suicideProbability() <= simulation.killedEnemies();
            }
        },
        ;

        /**
         * Does the simulation is allowed about the suicide probability ?
         *
         * @param simulation The cast simulation to check
         *
         * @return false if the cast must not be performed
         */
        public abstract boolean allow(CastSimulation simulation);
    }
}
