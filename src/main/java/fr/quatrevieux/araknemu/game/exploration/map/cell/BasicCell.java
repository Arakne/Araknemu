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

import fr.arakne.utils.maps.AbstractCellDataAdapter;
import fr.arakne.utils.maps.serializer.CellData;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * Simple cell type
 */
final public class BasicCell extends AbstractCellDataAdapter<ExplorationMap> implements ExplorationMapCell {
    public BasicCell(int id, CellData template, ExplorationMap map) {
        super(map, template, id);
    }

    @Override
    public boolean free() {
        // @todo check movement value
        if (!walkable()) {
            return false;
        }

        for (Creature creature : map().creatures()) {
            if (equals(creature.cell())) {
                return false;
            }
        }

        return true;
    }
}
