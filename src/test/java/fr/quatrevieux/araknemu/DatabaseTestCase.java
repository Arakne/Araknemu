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

package fr.quatrevieux.araknemu;


import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.helpers.NOPLogger;

import java.io.File;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTestCase extends TestCase {
    protected ConnectionPool connection;
    protected ConnectionPoolExecutor poolUtils;

    @BeforeEach
    public void setUp() throws Exception {
        connection =  new DefaultDatabaseHandler(
            new DefaultConfiguration(
                new IniDriver(
                    new Ini(new File("src/test/test_config.ini"))
                )
            ).module(DatabaseConfiguration.class),
            NOPLogger.NOP_LOGGER
        ).get("realm");

        poolUtils = new ConnectionPoolExecutor(connection);
    }

    public void assertTableExists(String tableName) throws SQLException {
        assertTrue((boolean) poolUtils.prepare(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            statement -> {
                statement.setString(1, tableName);

                return statement.executeQuery().next();
            }
        ));
    }

    public void dropTable(String tableName) throws SQLException {
        poolUtils.query("DROP TABLE "+tableName);
    }
}
