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

import fr.arakne.utils.maps.path.Pathfinder;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.util.NullnessUtil;

import java.util.Optional;

/**
 * Try to move near the selected enemy
 */
public final class MoveNearEnemy<F extends ActiveFighter> implements ActionGenerator<F> {
    private @MonotonicNonNull Pathfinder<BattlefieldCell> pathfinder;
    private @MonotonicNonNull AIHelper helper;

    @Override
    public void initialize(AI<F> ai) {
        this.helper = ai.helper();
        this.pathfinder = helper.cells().pathfinder()
            .targetDistance(1)
            .walkablePredicate(cell -> true) // Fix #94 Ignore inaccessible cell (handled by cell cost)
            .cellWeightFunction(this::cellCost)
        ;
    }

    @Override
    public Optional<Action> generate(AI<F> ai, AiActionFactory actions) {
        if (helper == null || !helper.canMove()) {
            return Optional.empty();
        }

        final int movementPoints = helper.movementPoints();
        final BattlefieldCell currentCell = ai.fighter().cell();

        return ai.enemy()
            .map(enemy -> NullnessUtil.castNonNull(pathfinder).findPath(currentCell, enemy.cell()).truncate(movementPoints + 1))
            .map(path -> path.keepWhile(step -> step.cell().equals(currentCell) || step.cell().walkable())) // Truncate path to first unwalkable cell (may occur if the enemy cell is inaccessible or if other fighters block the path)
            .filter(path -> path.size() > 1)
            .map(path -> actions.move(path))
        ;
    }

    /**
     * Compute the cell cost for optimize the path finding
     */
    private @Positive int cellCost(BattlefieldCell cell) {
        // Fix #94 : Some cells are not accessible, but walkable/targetable using teleport.
        // In this case the pathfinder will fail, so instead of ignoring unwalkable cells, simply set a very high cost,
        // which allows the AI to generate a path to an inaccessible cell without throws a PathException
        if (!cell.walkableIgnoreFighter()) {
            return 1000;
        }

        // A fighter is on the cell : the cell is not walkable
        // But the fighter may leave the place at the next turn
        // The cost is higher than a simple detour, but permit to resolve a path blocked by a fighter
        if (cell.hasFighter()) {
            return 15;
        }

        // Add a cost of 3 for each enemy around the cell
        // This cost corresponds to the detour cost + 1
        return 1 + Asserter.castNonNegative(3 * (int) NullnessUtil.castNonNull(helper).enemies().adjacent(cell).count());
    }
}
