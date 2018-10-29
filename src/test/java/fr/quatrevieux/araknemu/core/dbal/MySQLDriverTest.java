package fr.quatrevieux.araknemu.core.dbal;

import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import org.ini4j.Ini;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class MySQLDriverTest {
    @Test
    void newConnection() throws IOException, SQLException {
        MySQLDriver driver = new MySQLDriver(
            new DefaultConfiguration(new IniDriver(new Ini(new File("src/test/test_config.ini"))))
                .module(DatabaseConfiguration.class)
                .connection("test_mysql")
        );

        try (Connection connection = driver.newConnection()) {
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery("select 1");

                assertTrue(rs.next());

                assertEquals(1, rs.getInt("1"));
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
}
