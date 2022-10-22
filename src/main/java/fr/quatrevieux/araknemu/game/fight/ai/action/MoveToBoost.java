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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Optional;

/**
 * Try to move for perform the optimal boost
 *
 * The best cell for cast the boost is selected.
 * If the current cell permit boost and the fighter is surrounded by enemies, the fighter will not perform any move.
 *
 * For select the cell, the generator will iterate over all reachable cells
 * with the current amount of MPs,
 * and check all spells on all available cells.
 * The best effective cell and cast is selected.
 */
public final class MoveToBoost<F extends ActiveFighter> implements ActionGenerator<F> {
    private final MoveToCast<F> generator;
    private final Boost<F> action;

    public MoveToBoost(Simulator simulator) {
        action = Boost.allies(simulator);
        generator = new MoveToCast<>(simulator, action, new MoveToCast.BestTargetStrategy<>());
    }

    @Override
    public void initialize(AI<F> ai) {
        action.initialize(ai);
        generator.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI<F> ai, AiActionFactory actions) {
        return generator.generate(ai, actions);
    }
}
