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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.arakne.utils.maps.CoordinateCell;
import fr.arakne.utils.maps.DofusMap;
import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.constant.Direction;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for resolving effect areas
 */
final class Util {
    /**
     * The 4 directions in order of priority
     */
    private static final Direction[] DIRECTION_PRIORITY = new Direction[] {
        Direction.NORTH_EAST, Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_WEST,
    };

    private Util() {
        // Disable constructor
    }

    /**
     * Sort all cells of the given list by distance from the target cell,
     * and then by direction, clockwise starting from the north-east
     *
     * Note: this method will modify the given list
     *
     * @param target the target cell (i.e. center of the area)
     * @param cells the cells to sort
     *
     * @param <C> the cell type
     */
    public static <C extends MapCell> void sortCells(C target, List<C> cells) {
        final @SuppressWarnings("unchecked") CoordinateCell<C> center = target.coordinate();

        cells.sort((o1, o2) -> {
            final int firstDistance = center.distance(o1);
            final int secondDistance = center.distance(o2);

            if (firstDistance == secondDistance) {
                return compareDirections(center, o1.coordinate(), o2.coordinate());
            }

            return firstDistance - secondDistance;
        });
    }

    /**
     * Resolve adjacent cells and center cell
     * This is useful for circle or cross areas with size 1
     *
     * @param center the center of the area
     * @return Cells with the correct order
     *
     * @param <C> the cell type
     */
    public static <C extends MapCell> List<C> resolveCenterAndAdjacent(C center) {
        final List<C> set = new ArrayList<>(5);

        final int targetId = center.id();
        final @SuppressWarnings("unchecked") DofusMap<C> map = center.map();
        final int mapWidth = map.dimensions().width();
        final int mapSize = map.size();

        set.add(center);

        for (Direction direction : DIRECTION_PRIORITY) {
            final int neighborId = targetId + direction.nextCellIncrement(mapWidth);

            if (neighborId >= 0 && neighborId < mapSize) {
                set.add(map.get(neighborId));
            }
        }

        return set;
    }

    private static int compareDirections(CoordinateCell<?> from, CoordinateCell<?> a, CoordinateCell<?> b) {
        final Direction aDirection = fullDirection(from, a);
        final Direction bDirection = fullDirection(from, b);

        if (aDirection != bDirection) {
            // Add an offset of 1 to prioritize NORTH_EAST
            final int aDirectionOrder = (aDirection.ordinal() + 1) % 8;
            final int bDirectionOrder = (bDirection.ordinal() + 1) % 8;

            return aDirectionOrder - bDirectionOrder;
        }

        // Depending on direction, order by cell id
        if (aDirection == Direction.EAST || aDirection == Direction.NORTH || aDirection == Direction.NORTH_WEST || aDirection == Direction.NORTH_EAST) {
            return a.id() - b.id();
        } else {
            return b.id() - a.id();
        }
    }

    private static Direction fullDirection(CoordinateCell<?> from, CoordinateCell<?> target) {
        final @Nullable Direction exactDirection = fullExactDirection(from, target);

        if (exactDirection != null) {
            return exactDirection;
        }

        final int deltaX = target.x() - from.x();
        final int deltaY = target.y() - from.y();
        final int absDeltaX = Math.abs(deltaX);
        final int absDeltaY = Math.abs(deltaY);

        if (absDeltaX < absDeltaY) {
            if (deltaY < 0) {
                return deltaX < 0 ? Direction.NORTH : Direction.NORTH_EAST;
            }

            return deltaX < 0 ? Direction.SOUTH_WEST : Direction.SOUTH;
        }

        if (deltaX > 0) {
            return deltaY < 0 ? Direction.EAST : Direction.SOUTH_EAST;
        }

        return deltaY > 0 ? Direction.WEST : Direction.NORTH_WEST;
    }

    /**
     * Try to find the direction between two cells
     * If the direction doesn't match (i.e. it's not perfectly aligned), return null
     */
    private static @Nullable Direction fullExactDirection(CoordinateCell<?> from, CoordinateCell<?> target) {
        final int deltaX = target.x() - from.x();
        final int deltaY = target.y() - from.y();

        if (deltaX == 0) {
            return deltaY > 0 ? Direction.SOUTH_WEST : Direction.NORTH_EAST;
        }

        if (deltaY == 0) {
            return deltaX < 0 ? Direction.NORTH_WEST : Direction.SOUTH_EAST;
        }

        if (deltaX == deltaY) {
            return deltaX < 0 ? Direction.NORTH : Direction.SOUTH;
        }

        if (deltaX == -deltaY) {
            return deltaX < 0 ? Direction.WEST : Direction.EAST;
        }

        return null;
    }
}
