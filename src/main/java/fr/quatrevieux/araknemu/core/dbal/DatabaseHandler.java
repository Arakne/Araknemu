package fr.quatrevieux.araknemu.core.dbal;

import java.sql.SQLException;

/**
 * Handle database connections
 */
public interface DatabaseHandler {
    /**
     * Get a connection pool by its name
     * @param name
     * @return
     */
    public ConnectionPool get(String name) throws SQLException;
}
