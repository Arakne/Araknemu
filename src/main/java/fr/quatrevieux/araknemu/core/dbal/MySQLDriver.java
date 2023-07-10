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
 * Driver for MySQL connection
 */
public final class MySQLDriver implements Driver {
    private final DatabaseConfiguration.Connection configuration;

    public MySQLDriver(DatabaseConfiguration.Connection configuration) {
        this.configuration = configuration;
    }

    @Override
    public synchronized Connection newConnection() throws SQLException {
        return DriverManager.getConnection(
            jdbcUrl(),
            configuration.user(),
            configuration.password()
        );
    }

    @Override
    public String type() {
        return "mysql";
    }

    private String jdbcUrl() {
        return "jdbc:mariadb://" + configuration.host() + "/" + configuration.dbname() + "?useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true";
    }
}
