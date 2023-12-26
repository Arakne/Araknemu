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

import fr.arakne.utils.maps.CoordinateCell;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.util.Formula;
import fr.quatrevieux.araknemu.game.fight.ai.action.util.Movement;
import fr.quatrevieux.araknemu.game.fight.ai.proxy.ProxyAI;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.Optional;

/**
 * Move to the best cell to cast attraction spell
 * Will use {@link AttractEnemy} for compute the best cell
 *
 * Note: this generator should be registered after {@link AttractEnemy} to ensure that the move is performed
 * only if the spell cannot be cast from the current cell
 *
 * The move is performed only if the resulting distance with the enemy is lower than simply move to the enemy
 */
public final class MoveToAttractEnemy implements ActionGenerator {
    private final AttractEnemy selector;

    public MoveToAttractEnemy(AttractEnemy selector) {
        this.selector = selector;
    }

    @Override
    public void initialize(AI ai) {
        selector.initialize(ai);
    }

    @Override
    public <A extends Action> Optional<A> generate(AI ai, AiActionFactory<A> actions) {
        if (!selector.valid(ai)) {
            return Optional.empty();
        }

        final CoordinateCell<BattlefieldCell> currentCell = ai.fighter().cell().coordinate();
        final int lowestEnemyDistance = ai.helper().enemies().cells().mapToInt(currentCell::distance).min().orElse(Integer.MAX_VALUE);
        final int currentMovementPoints = ai.turn().points().movementPoints();
        final CastConstraintValidator<Spell> castSpellValidator = actions.castSpellValidator();

        final GenerationScope scope = new GenerationScope(
            ai,
            currentCell,
            castSpellValidator,
            lowestEnemyDistance,
            currentMovementPoints
        );

        return scope.toGenerator().generate(ai, actions);
    }

    private class GenerationScope {
        private final ProxyAI ai;
        private final CoordinateCell<BattlefieldCell> currentCell;
        private final CastConstraintValidator<Spell> castSpellValidator;
        private final int lowestEnemyDistance;
        private final int currentMovementPoints;

        public GenerationScope(AI ai, CoordinateCell<BattlefieldCell> currentCell, CastConstraintValidator<Spell> castSpellValidator, int lowestEnemyDistance, int currentMovementPoints) {
            this.ai = new ProxyAI(ai);
            this.currentCell = currentCell;
            this.castSpellValidator = castSpellValidator;
            this.lowestEnemyDistance = lowestEnemyDistance;
            this.currentMovementPoints = currentMovementPoints;
        }

        /**
         * Compute the move score for the given target
         *
         * Because higher score is better, and the best target is the one which result to the lowest enemy distance,
         * the score is the negative distance of computed {@link AttractEnemy.Result}.
         *
         * To prioritize the nearest move target, the score is also decreased by the distance between the current cell,
         * with a sigmoid function (ensures that the malus is always less than 1).
         * So the result distance can be retrieved negate the score, and applying ceil to ignore the sigmoid malus.
         *
         * If the result is not valid (i.e. {@link AttractEnemy.Result#valid()}, the score is {@link Double#NEGATIVE_INFINITY}.
         *
         * @param target The move target cell
         */
        public double score(CoordinateCell<BattlefieldCell> target) {
            final int castDistance = currentCell.distance(target);
            final AttractEnemy.Result result = selector.compute(ai.withPosition(target.id()), castSpellValidator);

            if (!result.valid()) {
                return Double.NEGATIVE_INFINITY;
            }

            return -result.distance() - Formula.sigmoid(castDistance);
        }

        /**
         * Check if the move target is valid for cast an attraction spell
         *
         * A move is considered valid if the result distance with the enemy (extracted from the score) is lower
         * than the current nearest enemy distance.
         * It also check that the result distance will be lower than if the character simply move to the enemy.
         */
        public boolean valid(Movement.ScoredCell scoredCell) {
            final int newDistance = (int) Math.ceil(-scoredCell.score());

            return newDistance < lowestEnemyDistance - currentMovementPoints;
        }

        /**
         * Create the movement generator
         */
        public Movement toGenerator() {
            final Movement movement = new Movement(this::score, this::valid);

            movement.initialize(ai);

            return movement;
        }
    }
}
