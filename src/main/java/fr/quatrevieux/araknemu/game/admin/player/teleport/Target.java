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
import org.checkerframework.checker.index.qual.IndexFor;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.dataflow.qual.Pure;

/**
 * The teleportation target
 *
 * @todo refactor with other teleportation actions
 */
public final class Target {
    private ExplorationMap map;
    private @NonNegative int cell;

    public Target(ExplorationMap map, @NonNegative int cell) {
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
    public void setCell(@NonNegative int cell) {
        this.cell = cell;
    }

    /**
     * Get the target map
     */
    @Pure
    public ExplorationMap map() {
        return map;
    }

    /**
     * Get the target cell
     * The target cell is always walkable
     */
    @SuppressWarnings("return") // Due to call to non-pure methods, check can't follow map() references
    public @NonNegative @IndexFor("map()") int cell() {
        final ExplorationMap map = map();
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
