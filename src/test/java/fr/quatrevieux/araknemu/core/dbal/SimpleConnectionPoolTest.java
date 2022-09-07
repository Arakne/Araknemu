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

import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import org.apache.logging.log4j.LogManager;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class SimpleConnectionPoolTest {
    private Driver driver;

    @BeforeEach
    void setUp() throws IOException {
        driver = new SQLiteDriver(
            new DefaultConfiguration(new IniDriver(new Ini(new File("src/test/test_config.ini"))))
                .module(DatabaseConfiguration.MODULE)
                .connection("realm")
        );
    }

    @Test
    void acquireWillCreateConnections() throws SQLException {
        SimpleConnectionPool pool = new SimpleConnectionPool(driver, 2, LogManager.getLogger());

        Connection connection1 = pool.acquire();
        Connection connection2 = pool.acquire();

        assertNotSame(connection1, connection2);

        connection1.close();
        connection2.close();
    }

    @Test
    void acquireAndRelease() throws SQLException {
        SimpleConnectionPool pool = new SimpleConnectionPool(driver, 1, LogManager.getLogger());

        try (Connection connection = pool.acquire()) {
            pool.release(connection);
            assertSame(connection, pool.acquire());
        }
    }

    @Test
    void releasePoolFull() throws SQLException {
        SimpleConnectionPool pool = new SimpleConnectionPool(driver, 1, LogManager.getLogger());

        Connection connection1 = pool.acquire();
        Connection connection2 = pool.acquire();

        pool.release(connection1);
        pool.release(connection2);

        assertEquals(1, pool.size());
        assertTrue(connection2.isClosed());
    }

    @Test
    void releaseClosedConnection() throws SQLException {
        SimpleConnectionPool pool = new SimpleConnectionPool(driver, 1, LogManager.getLogger());

        Connection connection = pool.acquire();
        connection.close();

        pool.release(connection);

        assertNotSame(connection, pool.acquire());
    }

    @Test
    void execute() throws SQLException {
        SimpleConnectionPool pool = new SimpleConnectionPool(driver, 1, LogManager.getLogger());

        assertTrue(pool.execute((ConnectionPool.Task<Boolean>) connection -> {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("create table test_table (`value` text)");
                stmt.executeUpdate("insert into test_table values ('FOO')");
            }

            return true;
        }));

        assertEquals("FOO", pool.execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("select * from test_table");

                rs.next();

                return rs.getString("value");
            }
        }));

        pool.execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("drop table test_table");
            }

            return true;
        });
    }

    @Test
    void stop() throws SQLException {
        SimpleConnectionPool pool = new SimpleConnectionPool(driver, 2, LogManager.getLogger());

        assertEquals(0, pool.size());

        pool.initialize();

        assertEquals(2, pool.size());

        Connection connection1 = pool.acquire();
        Connection connection2 = pool.acquire();

        pool.release(connection1);
        pool.release(connection2);

        pool.close();

        assertEquals(0, pool.size());

        assertTrue(connection1.isClosed());
        assertTrue(connection2.isClosed());
    }
}
