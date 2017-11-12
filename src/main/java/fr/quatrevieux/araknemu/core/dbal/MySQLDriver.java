package fr.quatrevieux.araknemu.core.dbal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Driver for MySQL connection
 */
final public class MySQLDriver implements Driver {
    final private DatabaseConfiguration.Connection configuration;

    public MySQLDriver(DatabaseConfiguration.Connection configuration) {
        this.configuration = configuration;
    }

    @Override
    synchronized public Connection newConnection() throws SQLException {
        return DriverManager.getConnection(
            jdbcUrl(),
            configuration.user(),
            configuration.password()
        );
    }

    @Override
    public String type() {
        return "mysql";
    }

    private String jdbcUrl() {
        return "jdbc:mysql://" + configuration.host() + "/" + configuration.dbname();
    }
}
