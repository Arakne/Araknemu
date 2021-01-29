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

package fr.quatrevieux.araknemu.game.exploration.area;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.Area;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.SubArea;
import fr.quatrevieux.araknemu.data.world.repository.environment.area.AreaRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.area.SubAreaRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.listener.player.InitializeAreas;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for handle areas
 */
final public class AreaService implements PreloadableService, EventsSubscriber {
    final private AreaRepository areaRepository;
    final private SubAreaRepository subAreaRepository;

    final private Map<Integer, ExplorationSubArea> subAreas = new HashMap<>();

    public AreaService(AreaRepository areaRepository, SubAreaRepository subAreaRepository) {
        this.areaRepository = areaRepository;
        this.subAreaRepository = subAreaRepository;
    }

    /**
     * Get all available areas
     */
    public Collection<ExplorationSubArea> list() {
        return subAreas.values();
    }

    /**
     * Get a sub area by its id
     */
    public ExplorationSubArea get(int id) {
        if (subAreas.containsKey(id)) {
            return subAreas.get(id);
        }

        final SubArea subArea = subAreaRepository.get(id);
        final Area area = areaRepository.get(subArea.area());

        ExplorationSubArea explorationSubArea = new ExplorationSubArea(subArea, area);
        subAreas.put(explorationSubArea.id(), explorationSubArea);

        return explorationSubArea;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading areas...");

        Map<Integer, Area> areas = areaRepository.all().stream().collect(Collectors.toMap(Area::id, Function.identity()));

        for (SubArea subArea : subAreaRepository.all()) {
            if (!areas.containsKey(subArea.area())) {
                throw new EntityNotFoundException("Cannot find area " + subArea.area());
            }

            subAreas.put(subArea.id(), new ExplorationSubArea(subArea, areas.get(subArea.area())));
        }

        logger.info("{} subareas loaded", subAreas.size());
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<PlayerLoaded>() {
                @Override
                public void on(PlayerLoaded event) {
                    event.player().dispatcher().add(new InitializeAreas(event.player(), AreaService.this));
                }

                @Override
                public Class<PlayerLoaded> event() {
                    return PlayerLoaded.class;
                }
            },
        };
    }
}
