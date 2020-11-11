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

/**
 * Handle AP and MP of a fighter turn
 */
public interface TurnPoints {
    /**
     * Get the current fighter movement points
     */
    public int movementPoints();

    /**
     * Add movement points to the current turn
     */
    public void addMovementPoints(int value);

    /**
     * Remove movement points to the current turn
     *
     * @return int The real removed MP amount
     */
    public int removeMovementPoints(int value);

    /**
     * Get the current fighter action points
     */
    public int actionPoints();

    /**
     * Add action points to the current turn
     */
    public void addActionPoints(int value);

    /**
     * Remove action points to the current turn
     *
     * @return int The real removed AP amount
     */
    public int removeActionPoints(int value);
}
