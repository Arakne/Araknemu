package fr.quatrevieux.araknemu.core.dbal;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Simple implementation of connection pool
 */
final public class SimpleConnectionPool implements ConnectionPool {
    final private Driver driver;
    final private BlockingQueue<Connection> connections;

    public SimpleConnectionPool(Driver driver, int size) {
        this.driver = driver;
        connections = new ArrayBlockingQueue<>(size);
    }

    @Override
    public void initialize() throws SQLException {
        int toInitialize = Math.min(connections.remainingCapacity(), 8);

        while (toInitialize-- > 0) {
            connections.offer(
                driver.newConnection()
            );
        }
    }

    @Override
    public Connection acquire() throws SQLException {
        if (connections.isEmpty()) {
            return driver.newConnection();
        }

        try {
            return connections.take();
        } catch (InterruptedException e) {
            return driver.newConnection();
        }
    }

    @Override
    public void release(Connection connection) {
        try {
            if (connection.isClosed()) {
                return;
            }
        } catch (SQLException e) { }

        // Cannot post the connection, close the connection
        if (!connections.offer(connection)) {
            try {
                connection.close();
            } catch (SQLException e) { }
        }
    }
}
