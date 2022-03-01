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

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import org.apache.logging.log4j.LogManager;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DefaultDatabaseHandlerTest extends TestCase {
    private DefaultDatabaseHandler handler;

    @BeforeEach
    void setUp() throws IOException {
        handler = new DefaultDatabaseHandler(
            new DefaultConfiguration(
                new IniDriver(
                    new Ini(new File("src/test/test_config.ini"))
                )
            ).module(DatabaseConfiguration.MODULE),
            LogManager.getLogger()
        );
    }

    @Test
    void getWithInvalidDriver() {
        assertThrows(IllegalArgumentException.class, () -> handler.get("invalid"));
    }

    @Test
    void getWillCreatePool() throws SQLException {
        ConnectionPool pool = handler.get("realm");

        pool.execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                return stmt.execute("create table test_table (`value` text)");
            }
        });

        pool.execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                return stmt.execute("insert into test_table values('FOO')");
            }
        });

        assertEquals("FOO", pool.execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("select * from test_table");

                rs.next();

                return rs.getString("value");
            }
        }));

        pool.execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                return stmt.execute("drop table test_table");
            }
        });
    }

    @Test
    void getWillRetrievePool() throws SQLException {
        assertSame(handler.get("realm"), handler.get("realm"));
    }

    @Test
    void getNotSamePool() throws SQLException {
        assertNotEquals(handler.get("realm"), handler.get("no_shared"));
    }

    @Test
    void getWithAutoReconnect() throws SQLException {
        assertInstanceOf(AutoReconnectConnectionPool.class, handler.get("test_ar"));
    }

    @Test
    void getWithRefreshPool() throws SQLException {
        assertInstanceOf(RefreshConnectionPool.class, handler.get("test_refresh"));
        handler.stop(); // Ensure that refresh task is stopped
    }

    @Test
    void stop() throws SQLException {
        ConnectionPool pool = handler.get("realm");

        assertSame(pool, handler.get("realm"));

        handler.stop();

        assertNotSame(pool, handler.get("realm"));
    }
}
