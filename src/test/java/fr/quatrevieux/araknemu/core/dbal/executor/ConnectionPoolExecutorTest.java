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

import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionPoolExecutorTest {
    private ConnectionPoolExecutor utils;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        utils = new ConnectionPoolExecutor(
            new DefaultDatabaseHandler(
                new DefaultConfiguration(
                    new IniDriver(
                        new Ini(new File("src/test/test_config.ini"))
                    )
                ).module(DatabaseConfiguration.MODULE),
                Mockito.mock(Logger.class)
            ).get("realm")
        );
    }

    @Test
    void query() throws SQLException {
        utils.query("create table test_table (`value` text)");

        assertTrue((boolean) utils.execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='test_table'");

                return rs.next();
            }
        }));

        utils.query("drop table test_table");
    }

    @Test
    void prepare() throws SQLException {
        utils.query("create table test_table (`value` text)");

        assertEquals(1, (int) utils.prepare("insert into test_table values(?)", stmt -> {
            stmt.setString(1, "FOO");
            return stmt.executeUpdate();
        }));

        assertEquals("FOO", utils.prepare(
            "select * from test_table where `value` = ?",
            stmt -> {
                stmt.setString(1, "FOO");

                ResultSet rs = stmt.executeQuery();

                rs.next();

                return rs.getString("value");
            }
        ));

        utils.query("drop table test_table");
    }

    @Test
    void size() throws SQLException {
        assertEquals(4, utils.size());

        utils.acquire();

        assertEquals(3, utils.size());
    }

    @Test
    void close() throws Exception {
        assertEquals(4, utils.size());

        Connection connection = utils.acquire();
        utils.release(connection);

        utils.close();
        assertEquals(0, utils.size());

        assertTrue(connection.isClosed());
    }
}