package fr.quatrevieux.araknemu.core.dbal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Driver for SQLite connection
 */
final public class SQLiteDriver implements Driver {
    final private DatabaseConfiguration.Connection configuration;

    public SQLiteDriver(DatabaseConfiguration.Connection configuration) {
        this.configuration = configuration;
    }

    @Override
    synchronized public Connection newConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl());
    }

    @Override
    public String type() {
        return "sqlite";
    }

    private String jdbcUrl() {
        String base = "jdbc:sqlite:";

        if (configuration.memory()) {
            if (!configuration.shared()) {
                return base + ":memory:";
            }

            return base + "file:" + configuration.name() + "?mode=memory&cache=shared";
        }

        return base + configuration.path();
    }
}
