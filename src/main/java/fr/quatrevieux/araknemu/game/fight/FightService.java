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

package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.event.GameStopped;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.builder.FightBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.FightBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.event.FightCreated;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.module.FightModule;
import fr.quatrevieux.araknemu.game.listener.player.exploration.LeaveExplorationForFight;
import fr.quatrevieux.araknemu.game.listener.player.fight.AttachFighter;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for create fights
 */
final public class FightService implements EventsSubscriber {
    final private MapTemplateRepository mapRepository;
    final private Dispatcher dispatcher;
    final private Map<Class, FightBuilderFactory> builderFactories;
    final private Collection<FightModule.Factory> moduleFactories;

    final private Map<Integer, Map<Integer, Fight>> fightsByMapId = new ConcurrentHashMap<>();
    final private AtomicInteger lastFightId = new AtomicInteger();

    public FightService(MapTemplateRepository mapRepository, Dispatcher dispatcher, Collection<? extends FightBuilderFactory> factories, Collection<FightModule.Factory> moduleFactories) {
        this.mapRepository = mapRepository;
        this.dispatcher = dispatcher;
        this.moduleFactories = moduleFactories;

        this.builderFactories = factories.stream().collect(
            Collectors.toMap(
                FightBuilderFactory::type,
                Function.identity()
            )
        );
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<PlayerLoaded>() {
                @Override
                public void on(PlayerLoaded event) {
                    event.player().dispatcher().add(new AttachFighter(event.player()));
                }

                @Override
                public Class<PlayerLoaded> event() {
                    return PlayerLoaded.class;
                }
            },
            new Listener<ExplorationPlayerCreated>() {
                @Override
                public void on(ExplorationPlayerCreated event) {
                    event.player().dispatcher().add(new LeaveExplorationForFight(event.player()));
                }

                @Override
                public Class<ExplorationPlayerCreated> event() {
                    return ExplorationPlayerCreated.class;
                }
            },
            new Listener<GameStopped>() {
                @Override
                public void on(GameStopped event) {
                    fightsByMapId.values().stream()
                        .flatMap(fights -> fights.values().stream())
                        .forEach(fight -> fight.cancel(true))
                    ;

                    fightsByMapId.clear();
                }

                @Override
                public Class<GameStopped> event() {
                    return GameStopped.class;
                }
            },
        };
    }

    /**
     * Create fight map
     *
     * @param map The base map
     */
    public FightMap map(ExplorationMap map) {
        return new FightMap(mapRepository.get(map.id()));
    }

    /**
     * Create the fight handler
     *
     * @param type The build type
     */
    @SuppressWarnings("unchecked")
    public <B extends FightBuilder> FightHandler<B> handler(Class<B> type) {
        return new FightHandler(
            this,
            builderFactories.get(type).create(this)
        );
    }

    /**
     * Get all fights on the map
     *
     * @param mapId The map id
     */
    public Collection<Fight> fightsByMap(int mapId) {
        if (fightsByMapId.containsKey(mapId)) {
            return fightsByMapId.get(mapId).values();
        }

        return Collections.emptyList();
    }

    /**
     * Get a fight by its id from a map
     *
     * @param mapId The map id
     * @param fightId The fight id
     */
    public Fight getFromMap(int mapId, int fightId) {
        if (!fightsByMapId.containsKey(mapId)) {
            throw new NoSuchElementException("Fight not found");
        }

        Map<Integer, Fight> fights = fightsByMapId.get(mapId);

        if (!fights.containsKey(fightId)) {
            throw new NoSuchElementException("Fight not found");
        }

        return fights.get(fightId);
    }

    /**
     * Generate a new unique fight id
     */
    int newFightId() {
        return lastFightId.incrementAndGet();
    }

    /**
     * The fight is initialized
     */
    synchronized void created(Fight fight) {
        if (fightsByMapId.containsKey(fight.map().id())) {
            fightsByMapId.get(fight.map().id()).put(fight.id(), fight);
        } else {
            Map<Integer, Fight> fights = new ConcurrentHashMap<>();

            fights.put(fight.id(), fight);

            fightsByMapId.put(fight.map().id(), fights);
        }

        dispatcher.dispatch(new FightCreated(fight));
    }

    /**
     * Remove the fight
     */
    synchronized void remove(Fight fight) {
        fightsByMapId.get(fight.map().id()).remove(fight.id());
    }

    /**
     * Make modules for a fight
     */
    Collection<FightModule> modules(Fight fight) {
        return moduleFactories.stream()
            .map(factory -> factory.create(fight))
            .collect(Collectors.toList())
        ;
    }
}
