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
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FightCreated;
import fr.quatrevieux.araknemu.game.listener.map.*;
import fr.quatrevieux.araknemu.game.listener.map.fight.*;
import fr.quatrevieux.araknemu.game.listener.player.SendMapData;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for handle exploration maps
 */
final public class ExplorationMapService implements PreloadableService, EventsSubscriber {
    final private MapTemplateRepository repository;
    final private FightService fightService;
    final private AreaService areaService;
    final private Dispatcher dispatcher;
    final private CellLoader loader;

    final private ConcurrentMap<Integer, ExplorationMap> maps = new ConcurrentHashMap<>();

    public ExplorationMapService(MapTemplateRepository repository, FightService fightService, AreaService areaService, Dispatcher dispatcher, CellLoader loader) {
        this.repository = repository;
        this.fightService = fightService;
        this.areaService = areaService;
        this.dispatcher = dispatcher;
        this.loader = loader;
    }

    /**
     * Load the exploration map
     */
    public ExplorationMap load(int mapId) throws EntityNotFoundException {
        if (!maps.containsKey(mapId)) {
            return createMap(repository.get(mapId));
        }

        return maps.get(mapId);
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading maps...");

        long start = System.currentTimeMillis();

        repository.all().forEach(this::createMap);

        long time = System.currentTimeMillis() - start;

        logger.info("{} maps successfully loaded in {}ms", maps.size(), time);
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<ExplorationPlayerCreated>() {
                @Override
                public void on(ExplorationPlayerCreated event) {
                    event.player().dispatcher().add(new SendMapData(event.player()));
                }

                @Override
                public Class<ExplorationPlayerCreated> event() {
                    return ExplorationPlayerCreated.class;
                }
            },

            new SendCreatedFight(this, fightService),
            new Listener<FightCreated>() {
                @Override
                public void on(FightCreated event) {
                    ExplorationMap map = load(event.fight().map().id());

                    event.fight().dispatcher().add(new HideFightOnStart(map));
                    event.fight().dispatcher().add(new SendFightsCount(map, fightService));
                    event.fight().dispatcher().add(new SendCancelledFight(map, fightService));
                    event.fight().dispatcher().add(new SendTeamFighterRemoved(map));
                    event.fight().dispatcher().add(new SendTeamFighterAdded(map));
                }

                @Override
                public Class<FightCreated> event() {
                    return FightCreated.class;
                }
            },
        };
    }

    /**
     * Load the exploration map
     */
    public ExplorationMap load(MapTemplate template) {
        if (maps.containsKey(template.id())) {
            return maps.get(template.id());
        }

        return createMap(template);
    }

    private ExplorationMap createMap(MapTemplate template) {
        ExplorationMap map = new ExplorationMap(template, loader, areaService.get(template.subAreaId()));
        maps.put(map.id(), map);

        map.dispatcher().add(new SendNewSprite(map));
        map.dispatcher().add(new SendSpriteRemoved(map));
        map.dispatcher().add(new SendPlayerChangeCell(map));
        map.dispatcher().add(new SendPlayerChangeOrientation(map));
        map.dispatcher().add(new SendCreatureMove(map));

        dispatcher.dispatch(new MapLoaded(map));

        return map;
    }
}
