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
public final class MoveToAttack implements ActionGenerator {
    private final Movement movement;
    private final Simulator simulator;
    private final Attack attack;
    private final TargetSelectionStrategy strategy;

    private final Map<FightCell, Collection<CastSimulation>> possibleActionsCache = new HashMap<>();

    private ActiveFighter fighter;
    private AIHelper helper;

    private MoveToAttack(Simulator simulator, TargetSelectionStrategy strategy) {
        this.simulator = simulator;
        this.attack = new Attack(simulator);
        this.strategy = strategy;
        this.movement = new Movement(this::score, this::isValidCell);
    }

    @Override
    public void initialize(AI ai) {
        movement.initialize(ai);
        attack.initialize(ai);

        this.fighter = ai.fighter();
    }

    @Override
    public Optional<Action> generate(AI ai) {
        helper = ai.helper();

        // Cannot move or attack
        if (!helper.canCast() || !helper.canMove()) {
            return Optional.empty();
        }

        try {
            // Can attack, but there is at least 1 enemy : do not perform move because of potential tackle
            if (canAttackFromCell(fighter.cell()) && helper.enemies().adjacent().findFirst().isPresent()) {
                return Optional.empty();
            }

            return movement.generate(ai);
        } finally {
            possibleActionsCache.clear();
        }
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

        possibleCasts = helper.withPosition(cell).spells()
            .simulate(simulator)
            .filter(attack::valid) // Keep only effective attacks
            .collect(Collectors.toList())
        ;

        possibleActionsCache.put(cell, possibleCasts);

        return possibleCasts;
    }

    /**
     * Check if there is at least one attack possible from the given cell
     */
    private boolean canAttackFromCell(FightCell cell) {
        return !computePossibleCasts(cell).isEmpty();
    }

    /**
     * Compute the score of a cell
     * The lowest value will be selected
     */
    private double score(CoordinateCell<FightCell> coordinate) {
        return strategy.score(this, coordinate);
    }

    /**
     * Check if the cell is a valid movement
     *
     * It must have at least one possible attack
     */
    private boolean isValidCell(Movement.ScoredCell scoredCell) {
        return canAttackFromCell(scoredCell.coordinates().cell());
    }

    /**
     * Select the nearest cell where a cast is possible
     *
     * Note: This selected cell is not the best cell for perform an attack, but the nearest cell.
     *       So, it do not perform the best move for maximize damage.
     */
    public static MoveToAttack nearest(Simulator simulator) {
        return new MoveToAttack(simulator, new NearestStrategy());
    }

    /**
     * Select the best target cell for cast a spell, and maximizing damage
     */
    public static MoveToAttack bestTarget(Simulator simulator) {
        return new MoveToAttack(simulator, new BestTargetStrategy());
    }

    public interface TargetSelectionStrategy {
        /**
         * Compute the score of a given target cell
         *
         * @param generator The action generator
         * @param target The cell to check
         *
         * @return The score as double. The highest value will be selected
         */
        public double score(MoveToAttack generator, CoordinateCell<FightCell> target);
    }

    public static final class BestTargetStrategy implements TargetSelectionStrategy {
        @Override
        public double score(MoveToAttack generator, CoordinateCell<FightCell> target) {
            return maxScore(generator, target.cell()) - target.distance(generator.fighter.cell());
        }

        /**
         * Compute the max spell score from the given cell
         */
        private static double maxScore(MoveToAttack generator, FightCell cell) {
            return generator.computePossibleCasts(cell).stream()
                .mapToDouble(generator.attack::score)
                .max().orElse(0)
            ;
        }
    }

    private static final class NearestStrategy implements TargetSelectionStrategy {
        @Override
        public double score(MoveToAttack generator, CoordinateCell<FightCell> target) {
            return -target.distance(generator.fighter.cell()) + sigmoid(BestTargetStrategy.maxScore(generator, target.cell()));
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
