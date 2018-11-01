package fr.quatrevieux.araknemu.core.dbal;

import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Base database handler
 */
final public class DefaultDatabaseHandler implements DatabaseHandler {
    final private DatabaseConfiguration configuration;
    final private Map<String, Driver.Factory> factories;
    final private Logger logger;

    final private Map<String, ConnectionPool> connections = new HashMap<>();

    public DefaultDatabaseHandler(DatabaseConfiguration configuration, Logger logger, Map<String, Driver.Factory> factories) {
        this.configuration = configuration;
        this.factories = factories;
        this.logger = logger;
    }

    public DefaultDatabaseHandler(DatabaseConfiguration configuration, Logger logger) {
        this(configuration, logger, new HashMap<>());

        factory("sqlite", SQLiteDriver::new);
        factory("mysql",  MySQLDriver::new);
    }

    public DefaultDatabaseHandler(DatabaseConfiguration configuration) {
        this(configuration, NOPLogger.NOP_LOGGER);
    }

    @Override
    public ConnectionPool get(String name) throws SQLException {
        if (connections.containsKey(name)) {
            return connections.get(name);
        }

        DatabaseConfiguration.Connection config = configuration.connection(name);

        ConnectionPool pool = new SimpleConnectionPool(
            factories.get(config.type()).create(config),
            config.maxPoolSize(),
            logger
        );

        if (config.refreshPoolInterval() > 0) {
            pool = new RefreshConnectionPool(pool, config.refreshPoolInterval(), logger);
        }

        if (config.autoReconnect()) {
            pool = new AutoReconnectConnectionPool(pool, logger);
        }

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

    @Override
    public void stop() {
        Collection<ConnectionPool> pools = new ArrayList<>(connections.values());
        connections.clear();

        for (ConnectionPool pool : pools) {
            try { pool.close(); } catch (Exception e) {}
        }
    }
}
