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

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.event.GameStarted;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellAction;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellActionFactory;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.listener.map.PerformCellActions;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handle exploration map triggers
 */
public final class MapTriggerService implements PreloadableService, EventsSubscriber {
    private final MapTriggerRepository repository;
    private final CellActionFactory actionFactory;

    private final Map<Integer, Collection<MapTrigger>> triggers = new HashMap<>();
    private boolean preloading = false;

    public MapTriggerService(MapTriggerRepository repository, CellActionFactory actionFactory) {
        this.repository = repository;
        this.actionFactory = actionFactory;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading map cells triggers...");

        final Collection<MapTrigger> mapTriggers = repository.all();

        for (MapTrigger trigger : mapTriggers) {
            if (!triggers.containsKey(trigger.map())) {
                triggers.put(trigger.map(), new ArrayList<>());
            }

            triggers.get(trigger.map()).add(trigger);
        }

        preloading = true;
        logger.info("{} triggers loaded", mapTriggers.size());
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<MapLoaded>() {
                @Override
                public void on(MapLoaded event) {
                    event.map().dispatcher().add(new PerformCellActions());
                }

                @Override
                public Class<MapLoaded> event() {
                    return MapLoaded.class;
                }
            },
            new Listener<GameStarted>() {
                @Override
                public void on(GameStarted event) {
                    preloading = false;
                }

                @Override
                public Class<GameStarted> event() {
                    return GameStarted.class;
                }
            },
        };
    }

    /**
     * Get all triggers for a map
     */
    public Collection<CellAction> forMap(ExplorationMap map) {
        if (!triggers.containsKey(map.id())) {
            // Triggers are preloaded but not found on this map
            if (preloading) {
                return Collections.emptyList();
            }

            triggers.put(map.id(), repository.findByMap(map.id()));
        }

        return triggers.get(map.id()).stream()
            .map(actionFactory::create)
            .collect(Collectors.toList())
        ;
    }
}
