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

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.BootException;
import fr.quatrevieux.araknemu.core.Service;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.core.network.Server;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.game.event.GameStarted;
import fr.quatrevieux.araknemu.game.event.GameStopped;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

/**
 * Service for game server
 */
public final class GameService implements Service {
    private final GameConfiguration configuration;
    private final RealmConnector connector;
    private final Server<GameSession> server;
    private final Logger logger;
    private final Collection<PreloadableService> preloadables;
    private final ListenerAggregate dispatcher;
    private final Collection<EventsSubscriber> subscribers;

    public GameService(GameConfiguration configuration, RealmConnector connector, Server<GameSession> server, Logger logger, ListenerAggregate dispatcher, Collection<PreloadableService> preloadables, Collection<EventsSubscriber> subscribers) {
        this.configuration = configuration;
        this.connector = connector;
        this.server = server;
        this.logger = logger;
        this.preloadables = preloadables;
        this.dispatcher = dispatcher;
        this.subscribers = subscribers;
    }

    @Override
    public void boot() throws BootException {
        final long startTime = System.currentTimeMillis();

        logger.info(
            "Starting game server {} at {}:{}",
            configuration.id(),
            configuration.ip(),
            configuration.port()
        );

        subscribe();

        for (PreloadableService service : preloadables) {
            service.preload(logger);
        }

        try {
            server.start();
        } catch (Exception e) {
            throw new BootException(e);
        }

        connector.declare(
            configuration.id(),
            configuration.port(),
            configuration.ip()
        );

        connector.updateState(
            configuration.id(),
            GameHost.State.ONLINE,
            true
        );

        dispatcher.dispatch(new GameStarted(this));
        logger.info("Game server {} started in {}ms", configuration.id(), System.currentTimeMillis() - startTime);
    }

    @Override
    public void shutdown() {
        logger.info("Stopping game server {}", configuration.id());

        connector.updateState(
            configuration.id(),
            GameHost.State.OFFLINE,
            false
        );

        dispatcher.dispatch(new GameStopped(this));

        try {
            server.stop();
        } catch (Exception e) {
            logger.error("Error during stopping socket server", e);
        }

        logger.info("Game server {} stopped", configuration.id());
    }

    /**
     * Get all active game sessions
     */
    public Collection<GameSession> sessions() {
        return server.sessions();
    }

    /**
     * Register subscribers
     */
    public void subscribe() {
        for (EventsSubscriber subscriber : subscribers) {
            dispatcher.register(subscriber);
        }
    }
}
