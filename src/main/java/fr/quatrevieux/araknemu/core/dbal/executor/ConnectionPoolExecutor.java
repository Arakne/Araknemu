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

package fr.quatrevieux.araknemu.core.dbal.executor;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for connection pool queries
 */
public final class ConnectionPoolExecutor implements ConnectionPool, QueryExecutor {
    private final ConnectionPool pool;

    public ConnectionPoolExecutor(ConnectionPool pool) {
        this.pool = pool;
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
    public <T> T prepare(String sql, PreparedTask<T> task, boolean returnGeneratedKeys) throws SQLException {
        return execute(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(
                sql,
                returnGeneratedKeys
                    ? PreparedStatement.RETURN_GENERATED_KEYS
                    : PreparedStatement.NO_GENERATED_KEYS
            )) {
                return task.execute(stmt);
            }
        });
    }

    @Override
    public void query(String sql) throws SQLException {
        this.<Void>execute(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }

            return null;
        });
    }

    @Override
    public <T> T execute(Task<T> task) throws SQLException {
        return pool.execute(task);
    }

    @Override
    public void close() throws Exception {
        pool.close();
    }
}
