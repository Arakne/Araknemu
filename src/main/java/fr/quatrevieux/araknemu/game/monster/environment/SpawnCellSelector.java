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

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;

/**
 * Strategy for select the group spawn cell
 */
public interface SpawnCellSelector {
    /**
     * Set the spawn map
     */
    public void setMap(ExplorationMap map);

    /**
     * Select the spawn cell
     */
    public ExplorationMapCell cell();

    /**
     * Create the cell selector for the given position
     *
     * If the cell is fixed (not -1 on {@link MonsterGroupPosition#position() cell}), a {@link FixedCellSelector} is returned
     * If not, {@link RandomCellSelector} is returned
     *
     * @param position The monster group position
     */
    public static SpawnCellSelector forPosition(Position position) {
        if (position.cell() == -1) {
            return new RandomCellSelector();
        }

        return new FixedCellSelector(position);
    }
}
