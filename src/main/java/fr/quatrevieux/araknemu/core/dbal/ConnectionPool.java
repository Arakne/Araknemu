package fr.quatrevieux.araknemu.core.dbal;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Handle pool of database connection for multi-threading optimisation
 * All implementation MUST be thread-safe
 */
public interface ConnectionPool {
    /**
     * Task for a connection
     * @param <T> Type of the result
     */
    static public interface Task<T> {
        /**
         * Perform a request into the pool
         * @param connection The acquired connection
         * @return The result of the task
         */
        public T perform(Connection connection) throws SQLException;
    }

    /**
     * Initialize the pool
     * Create connection and save into the pool
     */
    public void initialize() throws SQLException;

    /**
     * Acquire a connection instance from pool
     * If the pool is empty, a new connection will be created
     */
    public Connection acquire() throws SQLException;

    /**
     * Release a connection instance.
     * If the pool is full, the connection will be closed
     *
     * @param connection Connection to release to the pool
     */
    public void release(Connection connection);

    /**
     * Execute a connection pool task
     *
     * pool.execute( connection -> {
     *     ResultSet rs = connection.executeQuery("select * from xxx");
     *     List<Result> result = new ArrayList<>();
     *
     *     while (rs.next()) {
     *         result.add(makeFromResultSet(rs));
     *     }
     *
     *     return result;
     * });
     *
     * @param task Task to execute
     * @param <T> Result of the task
     */
    default public <T> T execute(Task<T> task) throws SQLException {
        Connection connection = acquire();

        try {
            return task.perform(connection);
        } finally {
            release(connection);
        }
    }
}
