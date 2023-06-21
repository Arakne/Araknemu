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
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AutoReconnectConnectionPoolTest {
    private AutoReconnectConnectionPool pool;
    private Logger logger;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        SQLiteDriver driver = new SQLiteDriver(
            new DefaultConfiguration(new IniDriver(new Ini(new File("src/test/test_config.ini"))))
                .module(DatabaseConfiguration.MODULE)
                .connection("realm")
        );

        logger = Mockito.mock(Logger.class);
        pool = new AutoReconnectConnectionPool(new SimpleConnectionPool(driver, 1, logger), logger);
        pool.initialize();
    }

    @AfterEach
    void tearDown() throws Exception {
        pool.close();
    }

    @Test
    void executeWithoutDisconnect() throws SQLException {
        int result = pool.execute(c -> {
            try (Statement stmt = c.createStatement()) {
                ResultSet rs = stmt.executeQuery("select 1");

                return rs.getInt("1");
            }
        });

        assertEquals(1, result);
        Mockito.verify(logger, Mockito.never()).warn(Mockito.anyString());
    }

    @Test
    void executeWithDisconnect() throws SQLException {
        Connection connection = pool.acquire();
        pool.release(connection);

        connection.close();

        int result = pool.execute(c -> {
            try (Statement stmt = c.createStatement()) {
                ResultSet rs = stmt.executeQuery("select 1");

                return rs.getInt("1");
            }
        });

        assertEquals(1, result);
        Mockito.verify(logger).warn("Recoverable SQL error occurs. Retry on a new connection");
    }

    @Test
    void executeNotDisconnectError() {
        assertThrows(SQLException.class, () -> pool.execute(c -> {
            try (Statement stmt = c.createStatement()) {
                ResultSet rs = stmt.executeQuery("invalid query");
                return rs.getInt("1");
            }
        }));

        Mockito.verify(logger, Mockito.never()).warn(Mockito.anyString());
    }

    @Test
    void size() throws SQLException {
        assertEquals(1, pool.size());

        Connection connection = pool.acquire();
        assertEquals(0, pool.size());

        pool.release(connection);
        assertEquals(1, pool.size());
    }

    @Test
    void cannotReconnect() {
        assertThrows(SQLException.class, () -> pool.execute(c -> {
            throw new SQLRecoverableException();
        }));

        Mockito.verify(logger, Mockito.times(2)).warn("Recoverable SQL error occurs. Retry on a new connection");
    }
}
