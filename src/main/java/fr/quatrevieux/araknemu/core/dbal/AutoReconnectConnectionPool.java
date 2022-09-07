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
import java.sql.SQLRecoverableException;

/**
 * Add auto-reconnect on connection pool
 */
public final class AutoReconnectConnectionPool implements ConnectionPool {
    private final ConnectionPool pool;
    private final Logger logger;

    public AutoReconnectConnectionPool(ConnectionPool pool, Logger logger) {
        this.pool = pool;
        this.logger = logger;
    }

    @Override
    public void initialize() throws SQLException {
        pool.initialize();
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
        SQLException lastError = null;

        for (int i = pool.size(); i >= 0; --i) {
            try {
                return pool.execute(task);
            } catch (SQLException e) {
                if (!causedByConnectionClosed(e)) {
                    throw e;
                }

                logger.warn("Recoverable SQL error occurs. Retry on a new connection");
                lastError = e;
            }
        }

        throw new SQLException("Max retry limit reached", lastError);
    }

    @Override
    public void close() throws Exception {
        pool.close();
    }

    /**
     * Check if the exception is caused by a closed connection
     */
    private boolean causedByConnectionClosed(SQLException exception) {
        return exception instanceof SQLRecoverableException
            || (exception.getMessage() != null && exception.getMessage().toLowerCase().contains("connection closed"))
        ;
    }
}
