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
import fr.quatrevieux.araknemu.game.GameConfiguration;
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
import fr.quatrevieux.araknemu.util.ExecutorFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for create fights
 */
public final class FightService implements EventsSubscriber {
    private final MapTemplateRepository mapRepository;
    private final Dispatcher dispatcher;
    private final Map<Class, FightBuilderFactory> builderFactories;
    private final Collection<FightModule.Factory> moduleFactories;
    private final GameConfiguration.FightConfiguration configuration;
    private final ScheduledExecutorService executor;

    private final Map<Integer, Map<Integer, Fight>> fightsByMapId = new ConcurrentHashMap<>();
    private final AtomicInteger lastFightId = new AtomicInteger();

    public FightService(MapTemplateRepository mapRepository, Dispatcher dispatcher, Collection<? extends FightBuilderFactory> factories, Collection<FightModule.Factory> moduleFactories, GameConfiguration.FightConfiguration configuration) {
        this.mapRepository = mapRepository;
        this.dispatcher = dispatcher;
        this.moduleFactories = moduleFactories;
        this.configuration = configuration;
        this.executor = ExecutorFactory.create(configuration.threadsCount());

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
                    executor.shutdownNow();
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
        final FightBuilderFactory<B> builderFactory = builderFactories.get(type);

        if (builderFactory == null) {
            throw new NoSuchElementException("Builder for fight type " + type.getSimpleName() + " is not registered");
        }

        return new FightHandler<>(this, builderFactory.create(this, executor));
    }

    /**
     * Get all fights on the map
     *
     * @param mapId The map id
     */
    public Collection<Fight> fightsByMap(int mapId) {
        final Map<Integer, Fight> fightsOnMap = fightsByMapId.get(mapId);

        if (fightsOnMap != null) {
            return fightsOnMap.values();
        }

        return Collections.emptyList();
    }

    /**
     * Get all available fights
     * Note: this method can be really heavy to execute
     */
    public Collection<Fight> fights() {
        return fightsByMapId.values().stream()
            .flatMap(fights -> fights.values().stream())
            .collect(Collectors.toList())
        ;
    }

    /**
     * Get a fight by its id from a map
     *
     * @param mapId The map id
     * @param fightId The fight id
     */
    public Fight getFromMap(int mapId, int fightId) {
        final Map<Integer, Fight> fights = fightsByMapId.get(mapId);
        final Fight fight;

        if (fights != null && (fight = fights.get(fightId)) != null) {
            return fight;
        }

        throw new NoSuchElementException("Fight not found");
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
        final Map<Integer, Fight> fights = fightsByMapId.computeIfAbsent(
            fight.map().id(),
            mapId -> new ConcurrentHashMap<>()
        );

        fights.put(fight.id(), fight);

        dispatcher.dispatch(new FightCreated(fight));
    }

    /**
     * Remove the fight
     */
    synchronized void remove(Fight fight) {
        final Map<Integer, Fight> fightsOnMap = fightsByMapId.get(fight.map().id());

        if (fightsOnMap != null) {
            fightsOnMap.remove(fight.id());
        }
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
