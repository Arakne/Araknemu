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

package fr.quatrevieux.araknemu.core.dbal;

import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Base database handler
 */
final public class DefaultDatabaseHandler implements DatabaseHandler {
    final private DatabaseConfiguration configuration;
    final private Map<String, Driver.Factory> factories;
    final private Logger logger;

    final private Map<String, ConnectionPool> connections = new HashMap<>();

    public DefaultDatabaseHandler(DatabaseConfiguration configuration, Logger logger, Map<String, Driver.Factory> factories) {
        this.configuration = configuration;
        this.factories = factories;
        this.logger = logger;
    }

    public DefaultDatabaseHandler(DatabaseConfiguration configuration, Logger logger) {
        this(configuration, logger, new HashMap<>());

        factory("sqlite", SQLiteDriver::new);
        factory("mysql",  MySQLDriver::new);
    }

    @Override
    public ConnectionPool get(String name) throws SQLException {
        if (connections.containsKey(name)) {
            return connections.get(name);
        }

        final DatabaseConfiguration.Connection config = configuration.connection(name);

        ConnectionPool pool = new SimpleConnectionPool(
            factories.get(config.type()).create(config),
            config.maxPoolSize(),
            logger
        );

        if (config.refreshPoolInterval() > 0) {
            pool = new RefreshConnectionPool(pool, config.refreshPoolInterval(), logger);
        }

        if (config.autoReconnect()) {
            pool = new AutoReconnectConnectionPool(pool, logger);
        }

        pool.initialize();

        connections.put(name, pool);

        return pool;
    }

    /**
     * Register a new factory
     *
     * @param type The factory type. {@link Driver#type()}
     * @param factory The factory for create the driver
     */
    public void factory(String type, Driver.Factory factory) {
        factories.put(type, factory);
    }

    @Override
    public void stop() {
        final Collection<ConnectionPool> pools = new ArrayList<>(connections.values());

        connections.clear();

        for (ConnectionPool pool : pools) {
            try {
                pool.close();
            } catch (Exception e) {
                // Stop is a no fail operation
            }
        }
    }
}
