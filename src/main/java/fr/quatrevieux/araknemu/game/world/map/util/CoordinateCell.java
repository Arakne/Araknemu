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
