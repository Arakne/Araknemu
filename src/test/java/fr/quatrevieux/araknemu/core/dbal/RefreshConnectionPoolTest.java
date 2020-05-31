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
import io.github.artsok.RepeatedIfExceptionsTest;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RefreshConnectionPoolTest {
    private RefreshConnectionPool pool;
    private Logger logger;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        SQLiteDriver driver = new SQLiteDriver(
            new DefaultConfiguration(new IniDriver(new Ini(new File("src/test/test_config.ini"))))
                .module(DatabaseConfiguration.class)
                .connection("realm")
        );

        logger = Mockito.mock(Logger.class);
        pool = new RefreshConnectionPool(new SimpleConnectionPool(driver, 2, logger), 0, logger);
        pool.initialize();
    }

    @AfterEach
    void tearDown() throws Exception {
        pool.close();
    }

    @Test
    void execute() throws SQLException {
        int result = pool.execute(connection -> {
            try (Statement stmt = connection.createStatement()) {
                return stmt.executeQuery("select 1").getInt("1");
            }
        });

        assertEquals(1, result);
    }

    @RepeatedIfExceptionsTest
    void refreshNoClosedConnections() throws InterruptedException {
        Thread.sleep(10);

        Mockito.verify(logger, Mockito.atLeast(1)).info("Refreshing pool");
        Mockito.verify(logger, Mockito.never()).info("Pool is empty : try to recreate {} connections", 1);
        Mockito.verify(logger, Mockito.never()).warn(Mockito.anyString());
    }

    @RepeatedIfExceptionsTest
    void refreshWithClosedConnections() throws Exception {
        Connection connection = pool.acquire();
        pool.release(connection);

        connection.close();

        Thread.sleep(10);

        Mockito.verify(logger, Mockito.atLeast(1)).info("Refreshing pool");
        Mockito.verify(logger).warn("Closed connection detected");
    }

    @RepeatedIfExceptionsTest
    void refreshWithEmptyPool() throws Exception {
        Connection connection1 = pool.acquire();
        pool.release(connection1);

        Connection connection2 = pool.acquire();
        pool.release(connection2);

        connection1.close();
        connection2.close();

        Thread.sleep(10);

        Mockito.verify(logger, Mockito.atLeast(1)).info("Refreshing pool");
        Mockito.verify(logger, Mockito.times(2)).warn("Closed connection detected");
        Mockito.verify(logger).info("Pool is empty : try to recreate {} connections", 2);
    }
}
