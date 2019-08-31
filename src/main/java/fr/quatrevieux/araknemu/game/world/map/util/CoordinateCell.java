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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.world.map.util;

import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.MapCell;

/**
 * Map cell with coordinates
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/ank/battlefield/utils/Pathfinding.as#L191
 */
final public class CoordinateCell<C extends MapCell> {
    final private C cell;

    final private int x;
    final private int y;

    public CoordinateCell(C cell) {
        this.cell = cell;

        int _loc4 = cell.map().dimensions().width();
        int _loc5 = cell.id() / (_loc4 * 2 - 1);
        int _loc6 = cell.id() - _loc5 * (_loc4 * 2 - 1);
        int _loc7 = _loc6 % _loc4;

        this.y = _loc5 - _loc7;
        this.x = (cell.id() - (_loc4 - 1) * this.y) / _loc4;
    }

    /**
     * Get the related map cell
     */
    public C cell() {
        return cell;
    }

    /**
     * Get the cell id
     */
    public int id() {
        return cell.id();
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    /**
     * Compute the direction to the target cell
     *
     * https://github.com/Emudofus/Dofus/blob/1.29/ank/battlefield/utils/Pathfinding.as#L204
     */
    public Direction directionTo(CoordinateCell<C> target) {
        if (x == target.x) {
            if (target.y > y) {
                return Direction.SOUTH_WEST;
            } else {
                return Direction.NORTH_EAST;
            }
        } else if (target.x > x) {
            return Direction.SOUTH_EAST;
        } else {
            return Direction.NORTH_WEST;
        }
    }

    /**
     * Get the cell distance
     */
    public int distance(CoordinateCell<C> target) {
        return Math.abs(x - target.x) + Math.abs(y - target.y);
    }
}
