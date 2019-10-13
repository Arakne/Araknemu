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

package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.core.BootException;
import fr.quatrevieux.araknemu.core.Service;
import fr.quatrevieux.araknemu.core.network.Server;
import org.slf4j.Logger;

/**
 * Service for handling server authentication
 */
final public class RealmService implements Service {
    final private RealmConfiguration configuration;
    final private Server server;
    final private Logger logger;

    public RealmService(RealmConfiguration configuration, Server server, Logger logger) {
        this.configuration = configuration;
        this.server = server;
        this.logger = logger;
    }

    @Override
    public void boot() throws BootException {
        try {
            server.start();
        } catch (Exception e) {
            throw new BootException("Cannot start realm server", e);
        }
    }

    @Override
    public void shutdown() {
        logger.info("Stopping realm service...");

        try {
            server.stop();
        } catch (Exception e) {
            logger.error("Error during stopping the realm service", e);
        }

        logger.info("Realm service stopped");
    }

    public RealmConfiguration configuration() {
        return configuration;
    }
}
