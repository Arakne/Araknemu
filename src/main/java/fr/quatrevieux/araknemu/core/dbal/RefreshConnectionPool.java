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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Perform regularly ping on connections for keeping alive, or refresh if connection is lost
 */
final public class RefreshConnectionPool implements ConnectionPool {
    final private ConnectionPool pool;
    final private long interval;
    final private Logger logger;

    final private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public RefreshConnectionPool(ConnectionPool pool, long interval, Logger logger) {
        this.pool = pool;
        this.interval = interval;
        this.logger = logger;
    }

    @Override
    public void initialize() throws SQLException {
        pool.initialize();
        executorService.schedule(this::refreshPool, interval, TimeUnit.SECONDS);
    }

    @Override
    public Connection acquire() throws SQLException {
        return pool.acquire();
    }

    @Override
    public void release(Connection connection) {
        pool.release(connection);
    }

    @Override
    public int size() {
        return pool.size();
    }

    @Override
    public <T> T execute(Task<T> task) throws SQLException {
        return pool.execute(task);
    }

    private void refreshPool() {
        logger.info("Refreshing pool");

        final int lastSize = pool.size();

        for (int i = 0; i < lastSize; ++i) {
            try {
                Connection connection = acquire();

                if (connection.isClosed()) {
                    logger.warn("Closed connection detected");
                    continue;
                }

                try (Statement statement = connection.createStatement()) {
                    statement.execute("SELECT 1");
                    release(connection);
                }
            } catch (SQLException e) {
                logger.warn("Closed connection detected");
            }
        }

        if (pool.size() == 0) {
            int newSize = Math.max(lastSize, 1);

            logger.info("Pool is empty : try to recreate {} connections", newSize);

            try {
                List<Connection> newConnections = new ArrayList<>();

                for (int i = 0; i < newSize; ++i) {
                    newConnections.add(acquire());
                }

                for (Connection connection : newConnections) {
                    release(connection);
                }
            } catch (SQLException e) {
                logger.warn("Cannot recreate the pool", e);
            }
        }

        executorService.schedule(this::refreshPool, interval, TimeUnit.SECONDS);
    }

    @Override
    public void close() throws Exception {
        executorService.shutdownNow();
        pool.close();
    }
}
