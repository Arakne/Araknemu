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
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.Pathfinder;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.util.Battlefield;
import fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.Positive;

import java.util.Optional;
import java.util.function.Function;

/**
 * Try to move near the selected fighter
 * If the current fighter is carried by the target one, it will try to move on an adjacent cell
 */
public final class MoveNearFighter implements ActionGenerator {
    private final Function<AI, Optional<? extends FighterData>> fighterResolver;
    private final boolean allowBlocking;

    public MoveNearFighter(Function<AI, Optional<? extends FighterData>> fighterResolver) {
        this(fighterResolver, true);
    }

    /**
     * @param fighterResolver The resolver for the target fighter
     * @param allowBlocking If true, allow the fighter to block the target by moving on the last free adjacent cell. Should be false for "move to ally" actions.
     */
    public MoveNearFighter(Function<AI, Optional<? extends FighterData>> fighterResolver, boolean allowBlocking) {
        this.fighterResolver = fighterResolver;
        this.allowBlocking = allowBlocking;
    }

    @Override
    public void initialize(AI ai) {

    }

    @Override
    public <A extends Action> Optional<A> generate(AI ai, AiActionFactory<A> actions) {
        if (!ai.helper().canMove()) {
            return Optional.empty();
        }

        final BattlefieldCell currentCell = ai.fighter().cell();

        return fighterResolver.apply(ai)
            .flatMap(target -> findTargetCell(target, currentCell.coordinate()))
            .flatMap(target -> generatePath(target, ai))
            .map(path -> path.keepWhile(step -> step.cell().equals(currentCell) || step.cell().walkable())) // Truncate path to first unwalkable cell (may occur if the enemy cell is inaccessible or if other fighters block the path)
            .filter(path -> path.size() > 1)
            .map(actions::move)
        ;
    }

    /**
     * Try to find the best target cell
     *
     * 3 cases are handled :
     * - allowBlocking is true (so the fighter can block the target) : the target cell is the cell of the target fighter.
     * - The target cell has at least 2 free adjacent cells : the target cell is the cell of the target fighter.
     * - Else, find the nearest free cell from the target (but not adjacent), and (if possible) closest to the current fighter
     *
     * @param target The target fighter
     * @param currentCell Current cell of the fighter controlled by AI
     *
     * @return The best target cell, or empty if no cell is found (very unlikely)
     */
    private Optional<BattlefieldCell> findTargetCell(FighterData target, CoordinateCell<BattlefieldCell> currentCell) {
        final BattlefieldCell targetCell = target.cell();

        if (allowBlocking) {
            return Optional.of(targetCell);
        }

        // There is at least 2 free adjacent cells around the target cell
        // So it will not be blocked by the current fighter
        if (Battlefield.freeAdjacentCellsCount(target.cell()) >= 2) {
            return Optional.of(targetCell);
        }

        final BattlefieldMap map = target.cell().map();
        final CoordinateCell<BattlefieldCell> targetCoordinate = targetCell.coordinate();
        final int mapSize = map.size();

        BattlefieldCell selectedCell = null;
        int minDistanceFromTarget = targetCoordinate.distance(currentCell);
        int minDistanceFromSelf = 0;

        for (int i = 0; i < mapSize; i++) {
            final BattlefieldCell cell = map.get(i);

            if (!cell.walkable()) {
                continue;
            }

            final int distance = targetCoordinate.distance(cell);

            // No adjacent cell is free, so ignore cells near the target
            if (distance < 2 || distance > minDistanceFromTarget) {
                continue;
            }

            final int distanceFromSelf = currentCell.distance(cell.coordinate());

            // Same distance from the target : select the cell closest to the current fighter
            if (distance == minDistanceFromTarget) {
                if (distanceFromSelf < minDistanceFromSelf) {
                    selectedCell = cell;
                    minDistanceFromSelf = distanceFromSelf;
                }

                continue;
            }

            // The cell is strictly closer to the target
            // so change the select cell and recompute the distance from the current fighter
            selectedCell = cell;
            minDistanceFromTarget = distance;
            minDistanceFromSelf = distanceFromSelf;
        }

        return Optional.ofNullable(selectedCell);
    }

    /**
     * Compute the cell cost for optimize the path finding
     */
    private @Positive int cellCost(BattlefieldCell cell, AIHelper helper) {
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
        return 1 + Asserter.castNonNegative(3 * (int) helper.enemies().adjacent(cell).count());
    }

    private Optional<Path<BattlefieldCell>> generatePath(BattlefieldCell targetCell, AI ai) {
        final AIHelper helper = ai.helper();
        final Pathfinder<BattlefieldCell> pathfinder = helper.cells().pathfinder()
            .targetDistance(targetCell.walkable() ? 0 : 1) // The target cell may be the cell of a fighter, so it's not walkable
            .walkablePredicate(cell -> true) // Fix #94 Ignore inaccessible cell (handled by cell cost)
            .cellWeightFunction(cell -> cellCost(cell, ai.helper()))
        ;

        final int movementPoints = helper.movementPoints();
        final BattlefieldCell currentCell = ai.fighter().cell();

        // The current fighter is not carried by the target fighter,
        // so the path is the path to the target
        if (!currentCell.equals(targetCell)) {
            return Optional.of(pathfinder.findPath(currentCell, targetCell).truncate(movementPoints + 1));
        }

        // The current fighter is carried by the target fighter
        // So we need to find a free adjacent cell
        return helper.cells().adjacentPath();
    }
}
