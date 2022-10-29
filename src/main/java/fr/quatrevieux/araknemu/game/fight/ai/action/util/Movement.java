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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action.util;

import fr.arakne.utils.maps.CoordinateCell;
import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathException;
import fr.arakne.utils.maps.path.Pathfinder;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.AiActionFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.util.NullnessUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

/**
 * Try to select the best move action
 *
 * - Select reachable (i.e. walkable cells in MP range) cells. Note : pathfinding is not performed, so it's not warranty that cells are really reachable
 * - Sort cell by the score (ascending, lower score cells selected first)
 * - Iterates selected cells
 * - Filter cells (using filter predicate)
 * - Try to find the path
 * - If a path is found, and can be performed by the current number of MPs, return the move action
 */
public final class Movement<F extends ActiveFighter> implements ActionGenerator<F> {
    private final ToDoubleFunction<CoordinateCell<BattlefieldCell>> scoreFunction;
    private final Predicate<ScoredCell> filter;

    private @MonotonicNonNull Pathfinder<BattlefieldCell> pathfinder;

    /**
     * Creates the Movement action generator
     *
     * @param scoreFunction The score function. The returned score is used for select the best cell. Higher score are selected first.
     * @param filter The selection cell filter
     */
    public Movement(ToDoubleFunction<CoordinateCell<BattlefieldCell>> scoreFunction, Predicate<ScoredCell> filter) {
        this.scoreFunction = scoreFunction;
        this.filter = filter;
    }

    @Override
    public void initialize(AI<F> ai) {
        this.pathfinder = new Decoder<>(ai.map()).pathfinder();
    }

    @Override
    public Optional<Action> generate(AI<F> ai, AiActionFactory actions) {
        final Pathfinder<BattlefieldCell> pathfinder = NullnessUtil.castNonNull(this.pathfinder);
        final int movementPoints = ai.turn().points().movementPoints();
        final List<ScoredCell> selectedCells = selectCells(ai, movementPoints);

        final CoordinateCell<BattlefieldCell> currentCell = ai.fighter().cell().coordinate();
        final ScoredCell currentCellScore = new ScoredCell(currentCell, scoreFunction.applyAsDouble(currentCell));
        final boolean currentCellIsValid = filter.test(currentCellScore);

        selectedCells.sort(ScoredCell::compareTo);

        for (ScoredCell cell : selectedCells) {
            if ((currentCellIsValid && currentCellScore.score() >= cell.score()) || !filter.test(cell)) {
                continue;
            }

            final Path<BattlefieldCell> path;

            try {
                path = pathfinder.findPath(ai.fighter().cell(), cell.coordinates.cell());
            } catch (PathException e) {
                // No valid path can be found
                continue;
            }

            // The path contains, as first step, the current cell.
            // So this steps must not be considered in MPs
            if (path.size() - 1 > movementPoints) {
                continue;
            }

            return Optional.of(actions.move(path));
        }

        return Optional.empty();
    }

    /**
     * Select all reachable cells for movement
     */
    private List<ScoredCell> selectCells(AI<F> ai, int movementPoints) {
        final CoordinateCell<BattlefieldCell> currentCell = ai.fighter().cell().coordinate();
        final List<ScoredCell> selectedCells = new ArrayList<>();

        for (BattlefieldCell cell : ai.map()) {
            if (!cell.walkable()) {
                continue;
            }

            final CoordinateCell<BattlefieldCell> coordinates = cell.coordinate();

            if (coordinates.distance(currentCell) > movementPoints) {
                continue;
            }

            selectedCells.add(new ScoredCell(coordinates, scoreFunction.applyAsDouble(coordinates)));
        }

        return selectedCells;
    }

    public static final class ScoredCell implements Comparable<ScoredCell> {
        private final CoordinateCell<BattlefieldCell> coordinates;
        private final double score;

        public ScoredCell(CoordinateCell<BattlefieldCell> coordinates, double score) {
            this.coordinates = coordinates;
            this.score = score;
        }

        /**
         * Get the cell coordinates
         */
        public CoordinateCell<BattlefieldCell> coordinates() {
            return coordinates;
        }

        /**
         * Get the cell score
         * Higher score are prioritized
         */
        public double score() {
            return score;
        }

        @Override
        public int compareTo(ScoredCell o) {
            return Double.compare(o.score, score);
        }
    }
}
