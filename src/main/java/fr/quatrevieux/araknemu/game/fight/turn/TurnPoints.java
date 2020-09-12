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

package fr.quatrevieux.araknemu.game.fight.turn;

public interface TurnPoints {
    /**
     * Get the current fighter movement points
     */
    int movementPoints();

    /**
     * Add movement points to the current turn
     */
    void addMovementPoints(int value);

    /**
     * Remove movement points to the current turn
     *
     * @return int The real removed MP amount
     */
    int removeMovementPoints(int value);

    /**
     * Get the current fighter action points
     */
    int actionPoints();

    /**
     * Add action points to the current turn
     */
    void addActionPoints(int value);

    /**
     * Remove action points to the current turn
     *
     * @return int The real removed AP amount
     */
    int removeActionPoints(int value);
}
