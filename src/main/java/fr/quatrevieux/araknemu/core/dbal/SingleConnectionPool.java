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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.dbal;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of connection pool allowing only one connection, and waiting for it
 * to be released before acquiring a new one
 */
public final class SingleConnectionPool implements ConnectionPool {
    private final Driver driver;
    private final Lock lock = new ReentrantLock();

    private @Nullable Connection connection;

    public SingleConnectionPool(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void initialize() throws SQLException {
        connection = driver.newConnection();
    }

    @Override
    public Connection acquire() throws SQLException {
        lock.lock();

        final Connection connection = this.connection;

        if (connection != null) {
            return connection;
        }

        return driver.newConnection();
    }

    @Override
    public void release(Connection connection) {
        try {
            this.connection = connection.isClosed() ? null : connection;
        } catch (SQLException e) {
            this.connection = null;
        } finally {
            lock.unlock(); // always unlock when release
        }
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void close() throws Exception {
        final Connection connection = this.connection;
        this.connection = null;

        if (connection != null) {
            connection.close();
        }
    }
}
