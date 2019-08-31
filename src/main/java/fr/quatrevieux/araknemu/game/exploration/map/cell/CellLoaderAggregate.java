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

package fr.quatrevieux.araknemu.game.exploration.map.cell;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregate cells loaders
 */
final public class CellLoaderAggregate implements CellLoader {
    final private CellLoader[] loaders;

    public CellLoaderAggregate(CellLoader[] loaders) {
        this.loaders = loaders;
    }

    @Override
    public Collection<ExplorationMapCell> load(ExplorationMap map, List<MapTemplate.Cell> cells) {
        return Arrays.stream(loaders)
            .flatMap(loader -> loader.load(map, cells).stream())
            .collect(Collectors.toList())
        ;
    }
}
