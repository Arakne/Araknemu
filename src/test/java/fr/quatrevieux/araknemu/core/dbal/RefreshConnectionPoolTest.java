package fr.quatrevieux.araknemu.core.dbal;

import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import org.ini4j.Ini;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

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
        pool = new RefreshConnectionPool(new SimpleConnectionPool(driver, 2), 0, logger);
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

    @Test
    void refreshNoClosedConnections() throws InterruptedException {
        Thread.sleep(10);

        Mockito.verify(logger, Mockito.atLeast(1)).info("Refreshing pool");
        Mockito.verify(logger, Mockito.never()).info("Pool is empty : try to recreate {} connections", 1);
        Mockito.verify(logger, Mockito.never()).warn(Mockito.anyString());
    }

    @Test
    void refreshWithClosedConnections() throws Exception {
        Connection connection = pool.acquire();
        pool.release(connection);

        connection.close();

        Thread.sleep(1);

        Mockito.verify(logger, Mockito.atLeast(1)).info("Refreshing pool");
        Mockito.verify(logger).warn("Closed connection detected");
    }

    @Test
    void refreshWithEmptyPool() throws Exception {
        Connection connection1 = pool.acquire();
        pool.release(connection1);

        Connection connection2 = pool.acquire();
        pool.release(connection2);

        connection1.close();
        connection2.close();

        Thread.sleep(1);

        Mockito.verify(logger, Mockito.atLeast(1)).info("Refreshing pool");
        Mockito.verify(logger, Mockito.times(2)).warn("Closed connection detected");
        Mockito.verify(logger).info("Pool is empty : try to recreate {} connections", 2);
    }
}
