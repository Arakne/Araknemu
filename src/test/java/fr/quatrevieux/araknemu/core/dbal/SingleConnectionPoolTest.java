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

import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;

class SingleConnectionPoolTest {
    private Driver driver;
    private SingleConnectionPool pool;

    @BeforeEach
    void setUp() throws IOException {
        driver = new SQLiteDriver(
            new DefaultConfiguration(new IniDriver(new Ini(new File("src/test/test_config.ini"))))
                .module(DatabaseConfiguration.MODULE)
                .connection("realm")
        );
        pool = new SingleConnectionPool(driver);
    }

    @Test
    void size() {
        assertEquals(1, pool.size());
    }

    @Test
    void initializeShouldCreateConnection() throws NoSuchFieldException, SQLException, IllegalAccessException {
        Field connectionField = SingleConnectionPool.class.getDeclaredField("connection");
        connectionField.setAccessible(true);

        assertNull(connectionField.get(pool));
        pool.initialize();
        assertNotNull(connectionField.get(pool));
    }

    @Test
    void acquireShouldReturnConnectionAndLock() throws SQLException, NoSuchFieldException, IllegalAccessException, InterruptedException {
        Field lockField = SingleConnectionPool.class.getDeclaredField("lock");
        lockField.setAccessible(true);

        pool.initialize();
        assertNotNull(pool.acquire());

        AtomicBoolean free = new AtomicBoolean(true);

        Thread thread = new Thread(() -> {
            try {
                free.set(Lock.class.cast(lockField.get(pool)).tryLock());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        thread.start();
        thread.join();

        assertFalse(free.get());
    }

    @Test
    void releaseShouldUnlock() throws SQLException, NoSuchFieldException, IllegalAccessException, InterruptedException {
        Field lockField = SingleConnectionPool.class.getDeclaredField("lock");
        lockField.setAccessible(true);

        pool.initialize();
        Connection connection = pool.acquire();
        pool.release(connection);

        AtomicBoolean free = new AtomicBoolean(true);

        Thread thread = new Thread(() -> {
            try {
                free.set(Lock.class.cast(lockField.get(pool)).tryLock());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        thread.start();
        thread.join();

        assertTrue(free.get());
    }

    @Test
    void releaseClosedConnectionShouldRemoveIt() throws SQLException, NoSuchFieldException, IllegalAccessException, InterruptedException {
        Field lockField = SingleConnectionPool.class.getDeclaredField("lock");
        lockField.setAccessible(true);

        Field connectionField = SingleConnectionPool.class.getDeclaredField("connection");
        connectionField.setAccessible(true);

        pool.initialize();
        Connection connection = pool.acquire();
        connection.close();
        pool.release(connection);

        AtomicBoolean free = new AtomicBoolean(true);

        Thread thread = new Thread(() -> {
            try {
                free.set(Lock.class.cast(lockField.get(pool)).tryLock());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        thread.start();
        thread.join();

        assertTrue(free.get());
        assertNull(connectionField.get(pool));
    }

    @Test
    void functionalCloseAndAcquire() throws SQLException {
        pool.initialize();

        Connection connection = pool.acquire();
        connection.close();

        pool.release(connection);

        assertNotSame(connection, pool.acquire());
        assertFalse(pool.acquire().isClosed());
    }

    @Test
    void closeShouldCloseAndRemoveConnection() throws Exception {
        Field connectionField = SingleConnectionPool.class.getDeclaredField("connection");
        connectionField.setAccessible(true);

        pool.initialize();

        Connection connection = pool.acquire();

        pool.close();

        assertTrue(connection.isClosed());
        assertNull(connectionField.get(pool));
    }

    @Test
    void closeAlreadyCloseShouldDoNothing() throws Exception {
        pool.close();
    }
}
