package fr.quatrevieux.araknemu.core.dbal;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Driver for connection
 */
public interface Driver {
    static public interface Factory {
        /**
         * Create the driver instance
         * @param configuration Connection configuration
         */
        public Driver create(DatabaseConfiguration.Connection configuration);
    }

    /**
     * Create a new connection
     */
    public Connection newConnection() throws SQLException;

    /**
     * Get the connection type
     */
    public String type();
}
