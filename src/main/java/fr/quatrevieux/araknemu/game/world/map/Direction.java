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

package fr.quatrevieux.araknemu.game.world.map;

import java.util.Arrays;
import java.util.function.Function;

/**
 * List of available directions
 */
public enum Direction {
    EAST(width -> 1),
    SOUTH_EAST(width -> width),
    SOUTH(width -> 2 * width - 1),
    SOUTH_WEST(width -> width - 1),
    WEST(width -> -1),
    NORTH_WEST(width -> -width),
    NORTH(width -> -(2 * width - 1)),
    NORTH_EAST(width -> -(width - 1));

    /**
     * Array of restricted directions (can be used on fight)
     */
    final static public Direction[] RESTRICTED = Arrays.stream(values()).filter(Direction::restricted).toArray(Direction[]::new);

    final private Function<Integer, Integer> computeNextCell;

    Direction(Function<Integer, Integer> computeNextCell) {
        this.computeNextCell = computeNextCell;
    }

    /**
     * Get the char value of the direction
     */
    public char toChar(){
        return (char) (ordinal() + 'a');
    }

    /**
     * Get the opposite direction
     */
    public Direction opposite(){
        return Direction.values()[(ordinal() + 4) % 8];
    }

    /**
     * Get the orthogonal direction
     */
    public Direction orthogonal() {
        return Direction.values()[(ordinal() + 2) % 8];
    }

    /**
     * Does the direction is restricted
     *
     * A restricted direction can be used in fight, or by monsters's sprites
     * Restricted direction do not allow diagonal
     */
    public boolean restricted() {
        return ordinal() % 2 == 1;
    }

    /**
     * Get the increment to apply to cell id for get the next cell on the direction
     *
     * Ex:
     * MapCell current = map.get(123);
     * MapCell east = map.get(Direction.EAST.nextCellIncrement(map.dimensions().width()) + current.id());
     *
     * @param mapWidth The map width
     *
     * @return The next cell id increment
     */
    public int nextCellIncrement(int mapWidth) {
        return computeNextCell.apply(mapWidth);
    }

    /**
     * Get the direction by its char value
     */
    static public Direction byChar(char c){
        return values()[c - 'a'];
    }
}
