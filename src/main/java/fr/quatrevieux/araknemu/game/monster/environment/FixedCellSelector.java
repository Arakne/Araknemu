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

package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

/**
 * Cell selector for fixed monster group
 */
public final class FixedCellSelector implements SpawnCellSelector {
    private final @NonNegative int cell;
    private @MonotonicNonNull ExplorationMap map;

    public FixedCellSelector(@NonNegative int cell) {
        this.cell = cell;
    }

    @Override
    public void setMap(ExplorationMap map) {
        this.map = map;
    }

    @Override
    public ExplorationMapCell cell() {
        final ExplorationMap map = this.map;

        if (map == null) {
            throw new IllegalStateException("Map must be loaded before");
        }

        if (map.size() <= cell) {
            throw new IllegalStateException("Invalid cell " + cell + " for map " + map.id());
        }

        return map.get(cell);
    }
}
