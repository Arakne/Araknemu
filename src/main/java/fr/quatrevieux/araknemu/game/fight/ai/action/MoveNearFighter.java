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

import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.Pathfinder;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.util.NullnessUtil;

import java.util.Optional;
import java.util.function.Function;

/**
 * Try to move near the selected fighter
 * If the current fighter is carried by the target one, it will try to move on an adjacent cell
 */
public final class MoveNearFighter<F extends ActiveFighter> implements ActionGenerator<F> {
    private @MonotonicNonNull Pathfinder<BattlefieldCell> pathfinder;
    private @MonotonicNonNull AIHelper helper;
    private final Function<AI<F>, Optional<? extends FighterData>> fighterResolver;

    public MoveNearFighter(Function<AI<F>, Optional<? extends FighterData>> fighterResolver) {
        this.fighterResolver = fighterResolver;
    }

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

        final BattlefieldCell currentCell = ai.fighter().cell();

        return fighterResolver.apply(ai)
            .flatMap(target -> generatePath(target, ai))
            .map(path -> path.keepWhile(step -> step.cell().equals(currentCell) || step.cell().walkable())) // Truncate path to first unwalkable cell (may occur if the enemy cell is inaccessible or if other fighters block the path)
            .filter(path -> path.size() > 1)
            .map(actions::move)
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

    private Optional<Path<BattlefieldCell>> generatePath(FighterData target, AI<F> ai) {
        final int movementPoints = NullnessUtil.castNonNull(helper).movementPoints();
        final BattlefieldCell currentCell = ai.fighter().cell();
        final BattlefieldCell targetCell = target.cell();

        // The current fighter is not carried by the target fighter,
        // so the path is the path to the target
        if (!currentCell.equals(targetCell)) {
            return Optional.of(NullnessUtil.castNonNull(pathfinder).findPath(currentCell, target.cell()).truncate(movementPoints + 1));
        }

        // The current fighter is carried by the target fighter
        // So we need to find a free adjacent cell
        return NullnessUtil.castNonNull(helper).cells().adjacentPath();
    }
}
