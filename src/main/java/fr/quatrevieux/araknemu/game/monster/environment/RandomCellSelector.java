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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.monster.environment;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Select a random cell from all free cells of the map
 */
public final class RandomCellSelector implements SpawnCellSelector {
    private static final RandomUtil RANDOM = RandomUtil.createShared();

    private @MonotonicNonNull ExplorationMap map;
    private int @MonotonicNonNull[] availableCells;

    @Override
    public void setMap(ExplorationMap map) {
        this.map = map;

        final List<Integer> freeCells = new ArrayList<>();

        for (int team = 0; team < 2; ++team) {
            for (ExplorationMapCell cell : map.fightPlaces(team)) {
                if (cell.walkable()) {
                    freeCells.add(cell.id());
                }
            }
        }

        availableCells = ArrayUtils.toPrimitive(RANDOM.shuffle(freeCells).toArray(new Integer[0]));

        if (availableCells.length == 0) {
            throw new IllegalArgumentException("No spawn position available on map " + map.id());
        }
    }

    @Override
    public ExplorationMapCell cell() {
        final ExplorationMap map = this.map;
        final int[] availableCells = this.availableCells;

        if (map == null || availableCells == null) {
            throw new IllegalStateException("Map must be loaded before");
        }

        final int offset = RANDOM.nextInt(availableCells.length);

        for (int i = 0; i < availableCells.length; ++i) {
            final int cellId = availableCells[(i + offset) % availableCells.length];

            final ExplorationMapCell cell = map.get(cellId);

            if (cell.free()) {
                return cell;
            }
        }

        throw new IllegalStateException("Cannot found a free cell on map " + map.id());
    }
}
