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

import fr.arakne.utils.maps.CoordinateCell;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.util.Movement;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.ActionsFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final Simulator simulator;
    private final Attack<F> attack;
    private final TargetSelectionStrategy<F> strategy;

    private MoveToAttack(Simulator simulator, TargetSelectionStrategy<F> strategy) {
        this.simulator = simulator;
        this.attack = new Attack<>(simulator);
        this.strategy = strategy;
    }

    @Override
    public void initialize(AI<F> ai) {
        attack.initialize(ai);
    }

    @Override
    public Optional<Action> generate(AI<F> ai, ActionsFactory<F> actions) {
        final AIHelper helper = ai.helper();
        final F fighter = ai.fighter();

        // Cannot move or attack
        if (fighter == null || !helper.canCast() || !helper.canMove()) {
            return Optional.empty();
        }

        final GenerationScope scope = new GenerationScope(ai.fighter(), actions, ai.helper());

        // Can attack, but there is at least 1 enemy : do not perform move because of potential tackle
        if (helper.enemies().adjacent().findFirst().isPresent() && scope.canAttackFromCell(fighter.cell())) {
            return Optional.empty();
        }

        final Movement<F> movement = new Movement<>(
            coordinates -> strategy.score(scope, coordinates),
            scoredCell -> scope.canAttackFromCell(scoredCell.coordinates().cell())
        );
        movement.initialize(ai);

        return movement.generate(ai, actions);
    }

    /**
     * Select the nearest cell where a cast is possible
     *
     * Note: This selected cell is not the best cell for perform an attack, but the nearest cell.
     *       So, it do not perform the best move for maximize damage.
     */
    public static <F extends ActiveFighter> MoveToAttack<F> nearest(Simulator simulator) {
        return new MoveToAttack<>(simulator, new NearestStrategy<>());
    }

    /**
     * Select the best target cell for cast a spell, and maximizing damage
     */
    public static <F extends ActiveFighter> MoveToAttack<F> bestTarget(Simulator simulator) {
        return new MoveToAttack<>(simulator, new BestTargetStrategy<>());
    }

    /**
     * Store parameters and possible actions of current action generator
     */
    public final class GenerationScope {
        private final F fighter;
        private final ActionsFactory<F> actions;
        private final AIHelper helper;
        private final Map<FightCell, Collection<CastSimulation>> possibleActionsCache = new HashMap<>();

        public GenerationScope(F fighter, ActionsFactory<F> actions, AIHelper helper) {
            this.fighter = fighter;
            this.actions = actions;
            this.helper = helper;
        }

        /**
         * Fighter handle by the AI, which will perform the action
         */
        public F fighter() {
            return fighter;
        }

        /**
         * Compute the score of the cast action
         * Higher is the score, more effective is the action
         */
        public double attackScore(CastSimulation simulation) {
            return attack.score(simulation);
        }

        /**
         * Check if there is at least one attack possible from the given cell
         */
        private boolean canAttackFromCell(FightCell cell) {
            return !computePossibleCasts(cell).isEmpty();
        }

        /**
         * Simulate possible attacks from the given cell
         *
         * - List available spells
         * - Combine with all accessible cells
         * - Check if the action is valid
         * - Simulate the action
         * - Keep only simulation results with an effective attack
         *
         * Note: Because the fighter should be move to the tested cell, values cannot be computed lazily, like with a stream
         *
         * @param cell The cell from which spells will be casted
         *
         * @see Attack#valid(CastSimulation) To check if the attack is effective
         */
        public Collection<CastSimulation> computePossibleCasts(FightCell cell) {
            Collection<CastSimulation> possibleCasts = possibleActionsCache.get(cell);

            if (possibleCasts != null) {
                return possibleCasts;
            }

            possibleCasts = helper.withPosition(cell).spells().caster(actions.cast().validator())
                .simulate(simulator)
                .filter(attack::valid) // Keep only effective attacks
                .collect(Collectors.toList())
            ;

            possibleActionsCache.put(cell, possibleCasts);

            return possibleCasts;
        }
    }

    public interface TargetSelectionStrategy<F extends ActiveFighter> {
        /**
         * Compute the score of a given target cell
         *
         * @param scope Scope which contains parameters for perform action selection
         * @param target The cell to check
         *
         * @return The score as double. The highest value will be selected
         */
        public double score(MoveToAttack<F>.GenerationScope scope, CoordinateCell<FightCell> target);
    }

    public static final class BestTargetStrategy<F extends ActiveFighter> implements TargetSelectionStrategy<F> {
        @Override
        public double score(MoveToAttack<F>.GenerationScope scope, CoordinateCell<FightCell> target) {
            return maxScore(scope, target.cell()) - target.distance(scope.fighter().cell());
        }

        /**
         * Compute the max spell score from the given cell
         */
        private static <F extends ActiveFighter> double maxScore(MoveToAttack<F>.GenerationScope scope, FightCell cell) {
            return scope.computePossibleCasts(cell).stream()
                .mapToDouble(scope::attackScore)
                .max().orElse(0)
            ;
        }
    }

    private static final class NearestStrategy<F extends ActiveFighter> implements TargetSelectionStrategy<F> {
        @Override
        public double score(MoveToAttack<F>.GenerationScope scope, CoordinateCell<FightCell> target) {
            return -target.distance(scope.fighter().cell()) + sigmoid(BestTargetStrategy.maxScore(scope, target.cell()));
        }

        /**
         * Transform score value in interval [-inf; +inf] to bounded value [0; 1]
         *
         * @param value Score to transform
         */
        private double sigmoid(double value) {
            return 0.5 + value / (2 * (1 + Math.abs(value)));
        }
    }
}
