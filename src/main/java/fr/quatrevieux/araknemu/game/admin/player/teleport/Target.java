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

package fr.quatrevieux.araknemu.game.admin.player.teleport;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

/**
 * The teleportation target
 *
 * @todo refactor with other teleportation actions
 */
final public class Target {
    private ExplorationMap map;
    private int cell;

    public Target(ExplorationMap map, int cell) {
        this.map = map;
        this.cell = cell;
    }

    /**
     * Define the target map
     */
    public void setMap(ExplorationMap map) {
        this.map = map;
    }

    /**
     * Define the target cell id
     * If the cell is not walkable, a walkable cell will be used instead
     */
    public void setCell(int cell) {
        this.cell = cell;
    }

    /**
     * Get the target map
     */
    public ExplorationMap map() {
        return map;
    }

    /**
     * Get the target cell
     * The target cell is always walkable
     */
    public int cell() {
        // @todo better algo : get nearest walkable cell
        // @todo save real cell
        if (cell < map.size() && map.get(cell).walkable()) {
            return cell;
        }

        // @todo utility class for find valid cell ?
        for (int cellId = 0; cellId < map.size(); ++cellId) {
            if (map.get(cellId).walkable()) {
                return cellId;
            }
        }

        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return map.geolocation() + " (" + map.id() + ") at cell " + cell();
    }
}
