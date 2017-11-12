package fr.quatrevieux.araknemu.core.dbal;

import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DefaultDatabaseHandlerTest {
    private DefaultDatabaseHandler handler;

    @BeforeEach
    void setUp() throws IOException {
        handler = new DefaultDatabaseHandler(
            new DefaultConfiguration(
                new IniDriver(
                    new Ini(new File("src/test/test_config.ini"))
                )
            ).module(DatabaseConfiguration.class)
        );
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
}