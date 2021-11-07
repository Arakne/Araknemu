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

package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger;

import fr.arakne.utils.maps.serializer.CellData;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Load trigger cells
 */
public final class TriggerLoader implements CellLoader {
    private final MapTriggerService service;

    public TriggerLoader(MapTriggerService service) {
        this.service = service;
    }

    @Override
    public Collection<ExplorationMapCell> load(ExplorationMap map, CellData[] cells) {
        return service.forMap(map).stream()
            .map(action -> new TriggerCell(action.cell(), action, map))
            .collect(Collectors.toList())
        ;
    }
}
