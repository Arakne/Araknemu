package fr.quatrevieux.araknemu.core.dbal.util;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class for connection pool queries
 */
final public class ConnectionPoolUtils implements ConnectionPool {
    public interface PreparedTask<T> {
        public T execute(PreparedStatement statement) throws SQLException;
    }

    final private ConnectionPool pool;

    public ConnectionPoolUtils(ConnectionPool pool) {
        this.pool = pool;
    }

    @Override
    public void initialize() throws SQLException {
        pool.initialize();
    }

    @Override
    public Connection acquire() throws SQLException {
        return pool.acquire();
    }

    @Override
    public void release(Connection connection) {
        pool.release(connection);
    }

    @Override
    public int size() {
        return pool.size();
    }

    /**
     * Prepare SQL query
     *
     * util.prepare("SELECT * FROM ACCOUNT WHERE ACCOUNT_ID = ?", stmt -> {
     *     stmt.setString(1, 123);
     *     ResultSet rs = stmt.executeQuery();
     *
     *     return load(rs);
     * }, false);
     *
     * @param sql SQL query to prepare
     * @param task Task to execute
     * @param returnGeneratedKeys Set true to return generated keys (like auto increment)
     * @param <T> The result type
     *
     * @return The result of the task
     *
     * @throws SQLException When error occurs during execution
     */
    public <T> T prepare(String sql, PreparedTask<T> task, boolean returnGeneratedKeys) throws SQLException {
        return execute(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(
                sql,
                returnGeneratedKeys
                    ? PreparedStatement.RETURN_GENERATED_KEYS
                    : PreparedStatement.NO_GENERATED_KEYS
            )) {
                return task.execute(stmt);
            }
        });
    }

    /**
     * Prepare SQL query
     *
     * util.prepare("SELECT * FROM ACCOUNT WHERE ACCOUNT_ID = ?", stmt -> {
     *     stmt.setString(1, 123);
     *     ResultSet rs = stmt.executeQuery();
     *
     *     return load(rs);
     * }, false);
     *
     * @param sql SQL query to prepare
     * @param task Task to execute
     * @param <T> The result type
     *
     * @return The result of the task
     *
     * @throws SQLException When error occurs during execution
     */
    public <T> T prepare(String sql, PreparedTask<T> task) throws SQLException {
        return prepare(sql, task, false);
    }

    /**
     * Execute simple SQL query
     *
     * @param sql Query to execute
     *
     * @throws SQLException When error occurs during execution
     */
    public void query(String sql) throws SQLException {
        execute(connection -> {
            try (Statement statement = connection.createStatement()){
                statement.execute(sql);
            }

            return null;
        });
    }

    @Override
    public <T> T execute(Task<T> task) throws SQLException {
        return pool.execute(task);
    }

    @Override
    public void close() throws Exception {
        pool.close();
    }
}
