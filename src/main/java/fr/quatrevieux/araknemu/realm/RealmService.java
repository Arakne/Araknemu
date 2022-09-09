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

package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.core.BootException;
import fr.quatrevieux.araknemu.core.InitializableService;
import fr.quatrevieux.araknemu.core.Service;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.core.network.Server;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.realm.event.AuthStopped;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

/**
 * Service for handling server authentication
 */
public final class RealmService implements Service {
    private final RealmConfiguration configuration;
    private final Server<RealmSession> server;
    private final Logger logger;
    private final Collection<InitializableService> preloadables;
    private final ListenerAggregate dispatcher;
    private final Collection<EventsSubscriber> subscribers;

    public RealmService(RealmConfiguration configuration, Server<RealmSession> server, Logger logger, ListenerAggregate dispatcher, Collection<InitializableService> preloadables, Collection<EventsSubscriber> subscribers) {
        this.configuration = configuration;
        this.server = server;
        this.logger = logger;
        this.dispatcher = dispatcher;
        this.preloadables = preloadables;
        this.subscribers = subscribers;
    }

    @Override
    public void boot() throws BootException {
        logger.info("Starting realm server on port {}", configuration.port());

        for (EventsSubscriber subscriber : subscribers) {
            dispatcher.register(subscriber);
        }

        for (InitializableService service : preloadables) {
            service.init(logger);
        }

        try {
            server.start();
        } catch (Exception e) {
            throw new BootException("Cannot start realm server", e);
        }

        logger.info("Realm server started");
    }

    @Override
    public void shutdown() {
        logger.info("Stopping realm service...");

        try {
            server.stop();
        } catch (Exception e) {
            logger.error("Error during stopping the realm service", e);
        }

        dispatcher.dispatch(new AuthStopped());
        logger.info("Realm service stopped");
    }

    public RealmConfiguration configuration() {
        return configuration;
    }

    public Collection<RealmSession> sessions() {
        return server.sessions();
    }
}
