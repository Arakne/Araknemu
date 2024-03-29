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

package fr.quatrevieux.araknemu.game.exploration.map.cell;

import fr.arakne.utils.maps.MapCell;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Cell of exploration map
 */
public interface ExplorationMapCell extends MapCell<ExplorationMapCell> {
    @Override
    public @NonNegative int id();

    @Override
    public ExplorationMap map();

    /**
     * Does the cell is free ?
     * A cell is free if a new sprite can be added on, and no other objects is present
     */
    public boolean free();

    /**
     * Apply an operation to all creatures on current cell, and return the first non-null operation value
     * If the operation return a value, the iteration will be stopped
     *
     * @see ExplorationCreature#apply(Operation)
     */
    public default <R> @Nullable R apply(Operation<R> operation)  {
        // Optimisation : the cell is not walkable, no creatures can be located here
        if (!walkable()) {
            return null;
        }

        for (ExplorationCreature creature : map().creatures()) {
            try {
                // Cell do not match : skip
                if (!equals(creature.cell())) {
                    continue;
                }
            } catch (IllegalStateException e) {
                // Creature may leave map (and cell) during application of Operation
                // which will cause an IllegalStateException (ex: change map or start fight)
                // So skip the creature
                continue;
            }

            final R result = creature.apply(operation);

            if (result != null) {
                return result;
            }
        }

        return null;
    }
}
