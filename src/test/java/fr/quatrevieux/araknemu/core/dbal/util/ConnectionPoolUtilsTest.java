package fr.quatrevieux.araknemu.core.dbal.util;

import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionPoolUtilsTest {
    private ConnectionPoolUtils utils;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        utils = new ConnectionPoolUtils(
            new DefaultDatabaseHandler(
                new DefaultConfiguration(
                    new IniDriver(
                        new Ini(new File("src/test/test_config.ini"))
                    )
                ).module(DatabaseConfiguration.class)
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
}