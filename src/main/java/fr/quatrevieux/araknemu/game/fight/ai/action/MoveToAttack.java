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
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;
import fr.quatrevieux.araknemu.game.fight.ai.util.SpellCaster;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Try to move for perform an attack
 *
 * The nearest cell for perform an attack is selected.
 * If the current cell permit to attack, the fighter will not perform any move.
 *
 * For select the cell, the generator will iterates over all reachable cells
 * with the current amount of MPs, sort them by distance,
 * and check all spells on all available cells.
 * The first matching cell is selected.
 */
public final class MoveToAttack implements ActionGenerator {
    private final Movement movement;
    private final Simulator simulator;
    private final Attack attack;
    private final TargetSelectionStrategy strategy;

    private SpellCaster caster;
    private ActiveFighter fighter;
    private int currentCellScore;
    private boolean canAttackFromCurrentCell;
    private AI ai;

    // @todo move farest and best spell + cache spell
    private MoveToAttack(Simulator simulator, TargetSelectionStrategy strategy) {
        this.simulator = simulator;
        this.attack = new Attack(simulator);
        this.strategy = strategy;
        this.movement = new Movement(
            this::score, // @todo score in double
            this::isValidCell
        );
    }

    @Override
    public void initialize(AI ai) {
        movement.initialize(ai);
        attack.initialize(ai);

        this.fighter = ai.fighter();
        this.caster = new SpellCaster(ai);
    }

    @Override
    public Optional<Action> generate(AI ai) {
        this.ai = ai;
        final int movementPoints = ai.turn().points().movementPoints();

        // Cannot move or attack
        if (movementPoints < 1 || ai.turn().points().actionPoints() < 1) {
            return Optional.empty();
        }

        // @todo check can perform move :
        // If can attack, but there is at least 1 enemy, do not perform move
        // If all adjacent cells are unwalkable (considering fighters), do not perform move

        // Compute the score of the current cell to compare with others
        final FightCell currentCell = fighter.cell();

        if (canAttackFromCurrentCell = canAttackFromCell(currentCell)) {
            currentCellScore = score(currentCell.coordinate());
        }

        return movement.generate(ai);
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
     *
     * @todo cache
     */
    public Collection<CastSimulation> computePossibleCasts(FightCell cell) {
        final FightCell lastCell = fighter.cell();
        final int actionPoints = ai.turn().points().actionPoints();

        try {
            // @todo refactor cast validation system
            fighter.move(cell);

            return StreamSupport.stream(fighter.spells().spliterator(), false)
                .filter(spell -> spell.apCost() <= actionPoints) // Spell requires too many AP
                .flatMap(spell -> StreamSupport.stream(ai.map().spliterator(), false)
                    .filter(FightCell::walkableIgnoreFighter) // Filter target cell
                    .filter(target -> caster.validate(spell, target)) // Validate spell (LoS, cooldown, target type...)
                    .map(target -> simulator.simulate(spell, fighter, target)) // Simulate cast
                    .filter(attack::valid) // Keep only effective attacks
                )
                .collect(Collectors.toList())
            ;
        } finally {
            // @todo refactor cast validation system
            fighter.move(lastCell);
        }
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
     *
     * @todo change score
     */
    private int score(CoordinateCell<FightCell> coordinate) {
        return strategy.score(this, coordinate);
    }

    /**
     * Check if the cell is a valid movement
     *
     * It must have an higher score than the current cell, and have at least one possible attack
     */
    private boolean isValidCell(Movement.ScoredCell scoredCell) {
        // @todo change to <= when score will be changed
        if (canAttackFromCurrentCell && scoredCell.score() >= currentCellScore) {
            return false;
        }

        return canAttackFromCell(scoredCell.coordinates().cell());
    }

    /**
     * Select the nearest cell where a cast is possible
     *
     * Note: This selected cell is not the best cell for perform an attack, but the nearest cell.
     *       So, it do not perform the best move for maximize damage.
     */
    public static MoveToAttack nearest(Simulator simulator) {
        return new MoveToAttack(simulator, (generator, target) -> target.distance(generator.fighter.cell()));
    }

    /**
     * Select the best target cell for cast a spell, and maximizing damage
     */
    public static MoveToAttack bestTarget(Simulator simulator) {
        return new MoveToAttack(
            simulator,
            // @fixme the score is negate because the lower score is selected. Should be changed
            (generator, target) ->
                (int) - generator.computePossibleCasts(target.cell()).stream()
                    .mapToDouble(generator.attack::score)
                    .max().orElse(0) // @fixme 0 ?
                + target.distance(generator.fighter.cell())
        );
    }

    public interface TargetSelectionStrategy {
        // @todo double
        public int score(MoveToAttack generator, CoordinateCell<FightCell> target);
    }
}
