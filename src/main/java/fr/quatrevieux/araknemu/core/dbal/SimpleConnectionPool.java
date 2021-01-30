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
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Simple implementation of connection pool
 */
final public class SimpleConnectionPool implements ConnectionPool {
    final private Driver driver;
    final private BlockingQueue<Connection> connections;
    final private Logger logger;

    public SimpleConnectionPool(Driver driver, int size, Logger logger) {
        this.driver = driver;
        this.logger = logger;
        this.connections = new ArrayBlockingQueue<>(size);
    }

    @Override
    public void initialize() throws SQLException {
        int toInitialize = Math.min(connections.remainingCapacity(), 8);

        while (toInitialize-- > 0) {
            connections.offer(
                driver.newConnection()
            );
        }
    }

    @Override
    public Connection acquire() throws SQLException {
        if (connections.isEmpty()) {
            logger.warn("Pool is empty : create a new connection. If this message occurs too many times consider increase poolSize value");
            return driver.newConnection();
        }

        try {
            return connections.take();
        } catch (InterruptedException e) {
            return driver.newConnection();
        }
    }

    @Override
    public void release(Connection connection) {
        try {
            if (connection.isClosed()) {
                return;
            }
        } catch (SQLException e) {
            // Ignore exception
        }

        // Cannot post the connection, close the connection
        if (!connections.offer(connection)) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Ignore: the failed connection is not kept here
            }
        }
    }

    @Override
    public int size() {
        return connections.size();
    }

    @Override
    public void close() {
        logger.info("Closing database connections...");

        final Collection<Connection> toClose = new ArrayList<>(connections);

        connections.clear();

        for (Connection connection : toClose) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Ignore: the failed connection is not kept here
            }
        }
    }
}
