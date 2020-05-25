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

package fr.quatrevieux.araknemu.game.world.map.util;

import fr.quatrevieux.araknemu.game.world.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.world.map.GameMap;

/**
 * Utility class for compute line of sights
 * Note: The used algorithm is not exactly same as the client's one, some differences can occurs
 *
 * Algorithm :
 * - Get coordinates of the two cells
 * - "draw" a line between the cells :
 * - Compute the vector between those cells
 * - Creates the function : Y = ySlope * X + yAtZero with
 * - For each X between the cells compute the related Y using the function
 * - Increments Y cell by cell to reach the computed Y
 * - Check the cell line of sight value
 * - If the sight is blocked, return false
 *
 * @param <C> The battlefield cell type
 */
final public class LineOfSight<C extends BattlefieldCell> {
    final private GameMap<C> battlefield;
    final private int width; // store map width for optimisation

    public LineOfSight(GameMap<C> battlefield) {
        this.battlefield = battlefield;
        this.width = battlefield.dimensions().width();
    }

    /**
     * Check the line of sight between those two cells
     *
     * @param source The source cell
     * @param target The target cell
     *
     * @return true if target is not blocked
     */
    public boolean between(C source, C target) {
        return between(new CoordinateCell<>(source), new CoordinateCell<>(target));
    }

    /**
     * Check the line of sight between those two cells
     *
     * @param source The source cell
     * @param target The target cell
     *
     * @return true if target is not blocked
     */
    public boolean between(CoordinateCell<C> source, CoordinateCell<C> target) {
        if (source.x() == target.x() && source.y() == target.y()) {
            return true;
        }

        if (source.x() == target.x()) {
            return checkWithSameX(source, target);
        }

        // Swap source and target to ensure that source.x < target.x
        if (source.x() > target.x()) {
            final CoordinateCell<C> tmp = source;

            source = target;
            target = tmp;
        }

        final int yDirection = source.y() > target.y() ? -1 : 1;
        final double ySlope = (double) (target.y() - source.y()) / (double) (target.x() - source.x());
        final double yAtZero = source.y() - ySlope * source.x();

        int currentY = source.y();

        // For every X between source and target
        for (int currentX = source.x(); currentX <= target.x(); ++currentX) {
            // yMax is the value of Y at the current X
            int yMax = (int) Math.round(currentX * ySlope + yAtZero);

            for (;;) {
                // target is reached : do not check it's LoS
                if (currentX == target.x() && currentY == target.y()) {
                    return true;
                }

                // Ignore the source LoS
                if (currentX != source.x() || currentY != source.y()) {
                    if (cellSightBlocking(currentX, currentY)) {
                        return false;
                    }
                }

                // Increment Y until yMax is reached
                if (currentY == yMax) {
                    break;
                }

                currentY += yDirection;
            }
        }

        // Increments Y until target is reached
        for (; yDirection > 0 ? currentY < target.y() : currentY > target.y(); currentY += yDirection) {
            if (cellSightBlocking(target.x(), currentY)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check the line of sight value for a single cell by its coordinates
     *
     * @return true if the cell block the line of sight
     */
    private boolean cellSightBlocking(int x, int y) {
        // https://github.com/Emudofus/Dofus/blob/1.29/ank/battlefield/utils/Pathfinding.as#L550
        final int cellId = x * width + y * (width - 1);

        // Cell outside the battlefield
        if (cellId >= battlefield.size()) {
            return false;
        }

        return battlefield.get(cellId).sightBlocking();
    }

    /**
     * Check line of sight with aligned cells
     */
    private boolean checkWithSameX(CoordinateCell<C> source, CoordinateCell<C> target) {
        final int startY = Math.min(source.y(), target.y());
        final int endY = Math.max(source.y(), target.y());

        // Ignore the first and last cells
        for (int currentY = startY + 1; currentY < endY; ++currentY) {
            if (cellSightBlocking(target.x(), currentY)) {
                return false;
            }
        }

        return true;
    }
}
