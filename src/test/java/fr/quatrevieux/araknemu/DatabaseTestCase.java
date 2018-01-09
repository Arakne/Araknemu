package fr.quatrevieux.araknemu;


import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import org.ini4j.Ini;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTestCase extends TestCase {
    protected ConnectionPool connection;
    protected ConnectionPoolUtils poolUtils;

    @BeforeEach
    public void setUp() throws Exception {
        connection =  new DefaultDatabaseHandler(
            new DefaultConfiguration(
                new IniDriver(
                    new Ini(new File("src/test/test_config.ini"))
                )
            ).module(DatabaseConfiguration.class)
        ).get("realm");

        poolUtils = new ConnectionPoolUtils(connection);
    }

    public void assertTableExists(String tableName) throws SQLException {
        assertTrue((boolean) poolUtils.prepare(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            statement -> {
                statement.setString(1, tableName);

                return statement.executeQuery().next();
            }
        ));
    }

    public void dropTable(String tableName) throws SQLException {
        poolUtils.query("DROP TABLE "+tableName);
    }
}
