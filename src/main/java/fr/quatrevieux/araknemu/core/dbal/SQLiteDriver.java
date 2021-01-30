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
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Driver for SQLite connection
 */
final public class SQLiteDriver implements Driver {
    final private DatabaseConfiguration.Connection configuration;

    public SQLiteDriver(DatabaseConfiguration.Connection configuration) {
        this.configuration = configuration;
    }

    @Override
    synchronized public Connection newConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl());
    }

    @Override
    public String type() {
        return "sqlite";
    }

    private String jdbcUrl() {
        final String base = "jdbc:sqlite:";

        if (configuration.memory()) {
            if (!configuration.shared()) {
                return base + ":memory:";
            }

            return base + "file:" + configuration.name() + "?mode=memory&cache=shared";
        }

        return base + configuration.path();
    }
}
