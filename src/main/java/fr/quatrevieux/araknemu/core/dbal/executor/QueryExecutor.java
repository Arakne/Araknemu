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

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Execute SQL queries
 */
public interface QueryExecutor {
    @FunctionalInterface
    interface PreparedTask<T> {
        public T execute(PreparedStatement statement) throws SQLException;
    }

    /**
     * Prepare SQL query
     *
     * executor.prepare("SELECT * FROM ACCOUNT WHERE ACCOUNT_ID = ?", stmt -> {
     *     stmt.setString(1, 123);
     *     ResultSet rs = stmt.executeQuery();
     *
     *     return load(rs);
     * }, false);
     *
     * @param sql SQL query to prepare
     * @param task Task to execute
     * @param returnGeneratedKeys Set true to return generated keys (like auto increment)
     * @param <T> The result type
     *
     * @return The result of the task
     *
     * @throws SQLException When error occurs during execution
     */
    public <T> T prepare(String sql, PreparedTask<T> task, boolean returnGeneratedKeys) throws SQLException;

    /**
     * Prepare SQL query
     *
     * executor.prepare("SELECT * FROM ACCOUNT WHERE ACCOUNT_ID = ?", stmt -> {
     *     stmt.setString(1, 123);
     *     ResultSet rs = stmt.executeQuery();
     *
     *     return load(rs);
     * }, false);
     *
     * @param sql SQL query to prepare
     * @param task Task to execute
     * @param <T> The result type
     *
     * @return The result of the task
     *
     * @throws SQLException When error occurs during execution
     */
    default public <T> T prepare(String sql, PreparedTask<T> task) throws SQLException {
        return prepare(sql, task, false);
    }

    /**
     * Execute simple SQL query
     *
     * @param sql Query to execute
     *
     * @throws SQLException When error occurs during execution
     */
    public void query(String sql) throws SQLException;
}
