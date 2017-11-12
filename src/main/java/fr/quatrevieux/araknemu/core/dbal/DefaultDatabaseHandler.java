package fr.quatrevieux.araknemu.core.dbal;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Base database handler
 */
final public class DefaultDatabaseHandler implements DatabaseHandler {
    final private DatabaseConfiguration configuration;
    final private Map<String, Driver.Factory> factories;

    final private Map<String, ConnectionPool> connections = new HashMap<>();

    public DefaultDatabaseHandler(DatabaseConfiguration configuration, Map<String, Driver.Factory> factories) {
        this.configuration = configuration;
        this.factories = factories;
    }

    public DefaultDatabaseHandler(DatabaseConfiguration configuration) {
        this(configuration, new HashMap<>());

        factory("sqlite", SQLiteDriver::new);
        factory("mysql",  MySQLDriver::new);
    }

    @Override
    public ConnectionPool get(String name) throws SQLException {
        if (connections.containsKey(name)) {
            return connections.get(name);
        }

        DatabaseConfiguration.Connection config = configuration.connection(name);

        ConnectionPool pool = new SimpleConnectionPool(
            factories.get(config.type()).create(config),
            config.maxPoolSize()
        );

        pool.initialize();

        connections.put(name, pool);

        return pool;
    }

    /**
     * Register a new factory
     * @param type
     * @param factory
     */
    public void factory(String type, Driver.Factory factory) {
        factories.put(type, factory);
    }
}
