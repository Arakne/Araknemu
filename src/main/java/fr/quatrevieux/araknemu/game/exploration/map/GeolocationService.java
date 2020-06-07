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

package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;

import java.util.Comparator;

/**
 * Handle map geolocation
 */
final public class GeolocationService {
    final static public class GeolocationContext {
        /** The first map is more pertinent */
        final static private int FIRST = -1;
        /** The second map is more pertinent */
        final static private int SECOND = 1;
        /** Both maps have the same pertinence */
        final static private int NONE = 0;

        private int superArea = 0;
        private Integer subArea;
        private boolean indoor = false;

        /**
         * Define the target super area
         */
        public GeolocationContext superArea(int superArea) {
            this.superArea = superArea;

            return this;
        }

        /**
         * Define the preferred sub area
         */
        public GeolocationContext subArea(int subArea) {
            this.subArea = subArea;

            return this;
        }

        /**
         * Define if the target map should be indoor
         */
        public GeolocationContext indoor(boolean indoor) {
            this.indoor = indoor;

            return this;
        }

        /**
         * Build the map comparator
         */
        private Comparator<MapTemplate> buildComparator() {
            Comparator<MapTemplate> comparator = this::compareSubArea;

            return comparator
                .thenComparing(this::compareIndoor)
                .thenComparing(this::compareSize)
            ;
        }

        /**
         * Compare the maps subareas
         * If only one of the two maps match with the context's subarea, it will be returned
         */
        private int compareSubArea(MapTemplate first, MapTemplate second) {
            if (subArea == null) {
                return NONE;
            }

            if (first.subAreaId() == subArea) {
                if (second.subAreaId() != subArea) {
                    return FIRST;
                }
            } else if (second.subAreaId() == subArea) {
                return SECOND;
            }

            return NONE;
        }

        /**
         * Compare the maps sizes
         * The bigger map will be returned
         */
        private int compareSize(MapTemplate first, MapTemplate second) {
            if (first.cells().length > second.cells().length) {
                return FIRST;
            }

            if (first.cells().length < second.cells().length) {
                return SECOND;
            }

            return NONE;
        }

        /**
         * Compare the maps indoor flag
         */
        private int compareIndoor(MapTemplate first, MapTemplate second) {
            if (first.indoor() == indoor) {
                if (second.indoor() != indoor) {
                    return FIRST;
                }
            } else if (second.indoor() == indoor) {
                return SECOND;
            }

            return NONE;
        }

        /**
         * Create a context from a map
         */
        static public GeolocationContext fromMap(ExplorationMap map) {
            return new GeolocationContext()
                .superArea(map.subArea().area().superarea())
                .subArea(map.subArea().id())
                .indoor(map.indoor())
            ;
        }
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
     * Find a map at a given geolocation
     *
     * Use a context for a more accurate result :
     * - Filter by the super area
     * - Search maps on the same sub area
     * - Search maps with same "indoor" flag
     * - Then, return the bigger map
     *
     * @param geolocation The map geolocation
     * @param context The search context
     *
     * @throws EntityNotFoundException When the map cannot be found at the given geolocation
     */
    public ExplorationMap find(Geolocation geolocation, GeolocationContext context) {
        return repository.byGeolocation(geolocation).stream()
            .filter(map -> areaService.get(map.subAreaId()).area().superarea() == context.superArea)
            .min(context.buildComparator())
            .map(mapService::load)
            .orElseThrow(() -> new EntityNotFoundException("map at position " + geolocation + "is not found"))
        ;
    }
}
