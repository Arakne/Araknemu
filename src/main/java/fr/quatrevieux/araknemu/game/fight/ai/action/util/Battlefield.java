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

package fr.quatrevieux.araknemu.game.fight.ai.action.util;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Utility functions for cells and map
 */
public final class Battlefield {
    private Battlefield() {
        // Disable instantiation
    }

    /**
     * Compute the count of free cells (i.e. walkable without fighter on it) around the given cell
     */
    public static @NonNegative int freeAdjacentCellsCount(BattlefieldCell cell) {
        return freeAdjacentCellsCount(cell.map(), cell);
    }

    /**
     * Compute the count of free cells (i.e. walkable without fighter on it) around the given cell
     */
    public static @NonNegative int freeAdjacentCellsCount(BattlefieldMap map, BattlefieldCell cell) {
        int walkableAdjacentCellsCount = 0;

        for (Direction direction : Direction.restrictedDirections()) {
            final int adjacentCellId = cell.id() + direction.nextCellIncrement(map.dimensions().width());

            if (adjacentCellId >= 0 && adjacentCellId < map.size() && map.get(adjacentCellId).walkable()) {
                ++walkableAdjacentCellsCount;
            }
        }

        return walkableAdjacentCellsCount;
    }
}
