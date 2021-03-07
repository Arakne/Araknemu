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

package fr.quatrevieux.araknemu.core.dbal;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Handle pool of database connection for multi-threading optimisation
 * All implementation MUST be thread-safe
 */
public interface ConnectionPool extends AutoCloseable {
    /**
     * Initialize the pool
     * Create connection and save into the pool
     */
    public void initialize() throws SQLException;

    /**
     * Acquire a connection instance from pool
     * If the pool is empty, a new connection will be created
     */
    public Connection acquire() throws SQLException;

    /**
     * Release a connection instance.
     * If the pool is full, the connection will be closed
     *
     * @param connection Connection to release to the pool
     */
    public void release(Connection connection);

    /**
     * Get the current size of the pool
     */
    public int size();

    /**
     * Execute a connection pool task
     *
     * <pre>{@code pool.execute( connection -> {
     *     ResultSet rs = connection.executeQuery("select * from xxx");
     *     List<Result> result = new ArrayList<>();
     *
     *     while (rs.next()) {
     *         result.add(makeFromResultSet(rs));
     *     }
     *
     *     return result;
     * });}</pre>
     *
     * @param task Task to execute
     * @param <T> Result of the task
     */
    public default <T> T execute(Task<T> task) throws SQLException {
        final Connection connection = acquire();

        try {
            return task.perform(connection);
        } finally {
            release(connection);
        }
    }

    /**
     * Task for a connection
     * @param <T> Type of the result
     */
    public static interface Task<T> {
        /**
         * Perform a request into the pool
         * @param connection The acquired connection
         * @return The result of the task
         */
        public T perform(Connection connection) throws SQLException;
    }
}
