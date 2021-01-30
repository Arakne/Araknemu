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

package fr.quatrevieux.araknemu.game.monster.environment;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Select a random cell from all free cells of the map
 */
final public class RandomCellSelector implements SpawnCellSelector {
    final static private RandomUtil RANDOM = RandomUtil.createShared();

    private ExplorationMap map;
    private int[] availableCells;

    @Override
    public void setMap(ExplorationMap map) {
        this.map = map;

        final List<Integer> freeCells = new ArrayList<>();

        for (int cellId = 0; cellId < map.size(); ++cellId) {
            final ExplorationMapCell cell = map.get(cellId);

            if (cell.free()) {
                freeCells.add(cellId);
            }
        }

        availableCells = ArrayUtils.toPrimitive(RANDOM.shuffle(freeCells).toArray(new Integer[0]));
    }

    @Override
    public ExplorationMapCell cell() {
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
