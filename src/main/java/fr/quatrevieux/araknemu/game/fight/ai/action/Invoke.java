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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.util.CastSpell;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.team.Team;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.util.NullnessUtil;

import java.util.Iterator;
import java.util.Optional;

/**
 * Invoke a creature
 *
 * This generator will prioritize healing when allies are low life.
 * It will also prioritize killing enemies.
 */
public final class Invoke implements ActionGenerator, CastSpell.SimulationSelector {
    private final CastSpell cast;
    private @Nullable AI ai;
    private double alliesLifeRatio = 1.0;

    @SuppressWarnings({"assignment", "argument"})
    public Invoke(Simulator simulator) {
        this.cast = new CastSpell(simulator, this);
    }

    @Override
    public void initialize(AI ai) {
        this.ai = ai;
        this.alliesLifeRatio = computeAlliesLifeRatio(ai);
    }

    @Override
    public <A extends Action> Optional<A> generate(AI ai, AiActionFactory<A> actions) {
        return cast.generate(ai, actions);
    }

    @Override
    public boolean valid(CastSimulation simulation) {
        return simulation.invocation() > 0
            && simulation.killedAllies() == 0
            && simulation.suicideProbability() == 0
        ;
    }

    @Override
    public double score(CastSimulation simulation) {
        double score = simulation.invocation();

        // Can kill an enemy : increase invocation score by the max life of the enemy
        if (simulation.killedEnemies() >= 0.99) {
            score += NullnessUtil.castNonNull(ai).enemy().map(fighter -> fighter.life().max()).orElse(0);
        }

        // Prioritize healing when allies are low life
        score += (2 - alliesLifeRatio) * (simulation.alliesLife() + simulation.selfLife());

        // Add direct damage to enemies
        score -= simulation.enemiesLife();

        return score / simulation.actionPointsCost();
    }

    private double computeAlliesLifeRatio(AI ai) {
        final Team<?> team = ai.fighter().team();

        double totalCurrentLife = 0;
        double totalMaxLife = 0;

        for (Iterator<? extends FighterData> it = ai.fighters().iterator(); it.hasNext();) {
            final FighterData fighter = it.next();

            if (fighter.team().equals(team)) {
                totalCurrentLife += fighter.life().current();
                totalMaxLife += fighter.life().max();
            }
        }

        if (totalMaxLife == 0) {
            return 1.0;
        }

        return totalCurrentLife / totalMaxLife;
    }
}
