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

package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.value.Geoposition;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;
import fr.quatrevieux.araknemu.game.exploration.area.ExplorationSubArea;

import java.util.Collection;

/**
 * Handle map geolocation
 */
final public class GeolocationService {
    final static public class GeopositionContext {
        public int superArea = 0;
        public Integer subArea;
    }

    final private ExplorationMapService mapService;
    final private AreaService areaService;
    final private MapTemplateRepository repository;

    public GeolocationService(ExplorationMapService mapService, AreaService areaService, MapTemplateRepository repository) {
        this.mapService = mapService;
        this.areaService = areaService;
        this.repository = repository;
    }

    /**
     * Find a map at a given geoposition
     *
     * Use a context for a more accurate result :
     * - Search maps on the same sub area
     * - Filter by the super area
     *
     * @param geoposition The map geoposition
     * @param context The search context
     *
     * @throws EntityNotFoundException When the map cannot be found at the given geoposition
     */
    public ExplorationMap find(Geoposition geoposition, GeopositionContext context) {
        Collection<MapTemplate> templates = repository.byGeoposition(geoposition);

        if (context.subArea != null) {
            for (MapTemplate template : templates) {
                if (template.subAreaId() == context.subArea) {
                    // @todo optimize
                    return mapService.load(template.id());
                }
            }
        }

        for (MapTemplate template : templates) {
            ExplorationSubArea explorationSubArea = areaService.get(template.subAreaId());

            if (explorationSubArea.area().superarea() == context.superArea) {
                // @todo optimize
                return mapService.load(template.id());
            }
        }

        throw new EntityNotFoundException("map at position " + geoposition + "is not found");
    }
}
