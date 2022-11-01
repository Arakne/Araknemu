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
 * Try to move for perform an attack
 *
 * The nearest cell for perform an attack is selected.
 * If the current cell permit attacking, the fighter will not perform any move.
 *
 * For select the cell, the generator will iterate over all reachable cells
 * with the current amount of MPs, sort them by distance,
 * and check all spells on all available cells.
 * The first matching cell is selected.
 */
public final class MoveToAttack<F extends ActiveFighter> implements ActionGenerator<F> {
    private final MoveToCast<F> generator;
    private final Attack<F> attack;

    private MoveToAttack(Simulator simulator, MoveToCast.TargetSelectionStrategy<F> strategy) {
        this.attack = new Attack<>(simulator);
        this.generator = new MoveToCast<>(simulator, attack, strategy);
    }

    @Override
    public void initialize(AI<F> ai) {
        attack.initialize(ai);
        generator.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI<F> ai, AiActionFactory actions) {
        return generator.generate(ai, actions);
    }

    /**
     * Select the nearest cell where a cast is possible
     *
     * Note: This selected cell is not the best cell for perform an attack, but the nearest cell.
     *       So, it do not perform the best move for maximize damage.
     */
    public static <F extends ActiveFighter> MoveToAttack<F> nearest(Simulator simulator) {
        return new MoveToAttack<>(simulator, new MoveToCast.NearestStrategy<>());
    }

    /**
     * Select the best target cell for cast a spell, and maximizing damage
     */
    public static <F extends ActiveFighter> MoveToAttack<F> bestTarget(Simulator simulator) {
        return new MoveToAttack<>(simulator, new MoveToCast.BestTargetStrategy<>());
    }
}
