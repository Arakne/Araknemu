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

package fr.quatrevieux.araknemu.game.exploration.interaction.map;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import org.checkerframework.checker.index.qual.IndexFor;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.dataflow.qual.Pure;

/**
 * The teleportation target
 * This class is immutable, so a new instance is created when changing the target map or cell
 *
 * Usage:
 * <pre>
 *     // Configure the target
 *     TeleportationTarget target = new TeleportationTarget(map, cell);
 *     target = target.withMap(newMap);
 *     target = target.withCell(newCell);
 *     target = target.ensureCellWalkable();
 *
 *     // Start the teleportation
 *     player.interactions().start(new Teleportation(player, target));
 * </pre>
 */
public final class TeleportationTarget {
    private final ExplorationMap map;
    private final @NonNegative int cell;

    public TeleportationTarget(ExplorationMap map, @NonNegative int cell) {
        this.map = map;
        this.cell = cell;
    }

    /**
     * Define the target map
     */
    public TeleportationTarget withMap(ExplorationMap map) {
        return new TeleportationTarget(map, cell);
    }

    /**
     * Define the target cell id
     *
     * Note: the validity of the cell is not checked, call {@link #ensureCellWalkable()} to ensure the cell is walkable
     */
    public TeleportationTarget withCell(@NonNegative int cell) {
        return new TeleportationTarget(map, cell);
    }

    /**
     * Ensure the target cell is valid and walkable
     * If the defined cell is not walkable, any walkable cell will be used instead
     *
     * @return a new instance with a valid walkable cell (can be the same instance if the cell is already valid)
     */
    public TeleportationTarget ensureCellWalkable() {
        final ExplorationMap map = map();

        // @todo better algo : get nearest walkable cell
        // @todo save real cell
        if (cell < map.size() && map.get(cell).walkable()) {
            return this;
        }

        // @todo utility class for find valid cell ?
        for (int cellId = 0; cellId < map.size(); ++cellId) {
            if (map.get(cellId).walkable()) {
                return new TeleportationTarget(map, cellId);
            }
        }

        throw new IllegalStateException("No walkable cell can be found on map " + map.id());
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
     *
     * The cell is not checked, so it can be invalid
     * Use {@link #ensureCellWalkable()} to ensure the cell is walkable
     */
    @Pure
    public @NonNegative @IndexFor("map()") int cell() {
        if (cell >= map().size()) {
            throw new IllegalStateException("Invalid cell " + cell + " for map " + map.id());
        }

        return cell;
    }

    @Override
    public String toString() {
        return map.geolocation() + " (" + map.id() + ") at cell " + cell();
    }

    /**
     * Apply the teleportation to the player
     * This is a shortcut for {@code player.interactions().start(new Teleportation(player, this));}
     *
     * @param player the player to teleport
     */
    public void apply(ExplorationPlayer player) {
        player.interactions().start(new Teleportation(player, this));
    }
}
