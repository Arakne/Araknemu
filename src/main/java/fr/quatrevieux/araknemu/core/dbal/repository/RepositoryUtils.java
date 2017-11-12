package fr.quatrevieux.araknemu.core.dbal.repository;

import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for repository
 *
 * @param <E> The entity class
 */
public class RepositoryUtils<E> {
    public interface Loader<E> {
        /**
         * Create an entity from database data
         * The created entity MUST be filled
         *
         * @param rs Database data
         *
         * @return The created entity
         *
         * @throws SQLException
         */
        public E create(ResultSet rs) throws SQLException;

        /**
         * Fill the entity with generated keys
         *
         * @param entity Entity to fill
         * @param keys The generated key {@link Statement#getGeneratedKeys()}
         *
         * @return The filled entity instance (may be new instance)
         *
         * @throws SQLException
         */
        public E fillKeys(E entity, ResultSet keys) throws SQLException;
    }

    public interface Binder {
        /**
         * Bind data into PreparedStatement
         */
        public void bind(PreparedStatement rs) throws SQLException;
    }

    final private ConnectionPoolUtils pool;
    final private Loader<E> loader;

    public RepositoryUtils(ConnectionPoolUtils pool, Loader<E> loader) {
        this.pool = pool;
        this.loader = loader;
    }

    /**
     * Find one entity from connection
     *
     * util.findOne(
     *     "SELECT * FROM ACCOUNT WHERE USERNAME = ?",
     *     rs -> rs.setString(1, "username")
     * );
     *
     * @param query The find query
     * @param binder The binder
     *
     * @return The new entity
     *
     * @throws RepositoryException When error occurs during query execution
     * @throws EntityNotFoundException If the entity cannot be found
     *
     * @see RepositoryUtils#findAll(String, Binder)  For find all entities
     */
    public E findOne(String query, Binder binder) throws RepositoryException {
        try {
            return pool.prepare(
                query,
                statement -> {
                    binder.bind(statement);

                    ResultSet rs = statement.executeQuery();

                    if (!rs.next()) {
                        throw new EntityNotFoundException();
                    }

                    return loader.create(rs);
                }
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Find all entities from connection
     *
     * util.findAll(
     *     "SELECT * FROM ACCOUNT WHERE PSEUDO LIKE ?",
     *     rs -> rs.setString(1, "%pseudo%")
     * );
     *
     * @param query The find query
     * @param binder The binder
     *
     * @return The found entity
     *
     * @throws RepositoryException When error occurs during query execution
     *
     * @see RepositoryUtils#findOne(String, Binder) For find only one entity
     */
    public List<E> findAll(String query, Binder binder) throws RepositoryException {
        try {
            return pool.prepare(
                query,
                statement -> {
                    binder.bind(statement);

                    ResultSet rs = statement.executeQuery();

                    List<E> result = new ArrayList<>();

                    while (rs.next()) {
                        result.add(loader.create(rs));
                    }

                    return result;
                }
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Execute aggregation function, like COUNT(*)
     * The aggregation function MUST be the first selected data
     *
     * util.aggregate(
     *     "SELECT COUNT(*) FROM ACCOUNT WHERE PSEUDO = ?",
     *     rs -> rs.setString(1, "pseudo")
     * );
     *
     * @param query The aggregation query
     * @param binder The binder
     *
     * @return Aggregation result
     *
     * @throws RepositoryException When error occurs during query execution
     */
    public int aggregate(String query, Binder binder) throws RepositoryException {
        try {
            return pool.prepare(
                query,
                statement -> {
                    binder.bind(statement);

                    ResultSet rs = statement.executeQuery();

                    if (!rs.next()) {
                        throw new RepositoryException("Invalid aggregate query");
                    }

                    return rs.getInt(1);
                }
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Execute update query
     *
     * util.update(
     *     "UPDATE ACCOUNT SET PSEUDO = ? WHERE ACCOUNT_ID = ?",
     *     rs -> {
     *         rs.setString("newPseudo");
     *         rs.setInt(123);
     *     }
     * );
     *
     * @param query The update query
     * @param binder The binder
     *
     * @return Number of affected rows
     *
     * @throws RepositoryException When error occurs during query execution
     */
    public int update(String query, Binder binder) throws RepositoryException {
        try {
            return pool.prepare(
                query,
                statement -> {
                    binder.bind(statement);
                    return statement.executeUpdate();
                }
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    /**
     * Execute update query and fill entity with generated keys (for INSERT query)
     *
     * account = util.update(
     *     "INSERT INTO ACCOUNT (NAME) VALUES (?)",
     *     rs -> rs.setString("name"),
     *     account
     * );
     *
     * account.id(); // new id
     *
     * @param query The update / insert query
     * @param binder The binder
     * @param entity The entity to fill
     *
     * @return The inserted / updated entity
     *
     * @throws RepositoryException When error occurs during query execution
     */
    public E update(String query, Binder binder, E entity) throws RepositoryException {
        try {
            return pool.prepare(
                query,
                statement -> {
                    binder.bind(statement);
                    statement.execute();

                    ResultSet rs = statement.getGeneratedKeys();

                    if (!rs.next()) {
                        throw new RepositoryException("No generated keys. Use RepositoryUtils#update(String, Binder) instead");
                    }

                    return loader.fillKeys(entity, rs);
                }
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }
}
