/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.core.dbal.repository;

import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;

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
         * @throws SQLException Throws by ResultSet
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
         * @throws SQLException Throws by ResultSet
         */
        public E fillKeys(E entity, ResultSet keys) throws SQLException;
    }

    public interface Binder {
        /**
         * Do nothing binder
         */
        final static public Binder NOP_BINDER = statement -> {};

        /**
         * Bind data into PreparedStatement
         */
        public void bind(PreparedStatement statement) throws SQLException;
    }

    final private QueryExecutor executor;
    final private Loader<E> loader;

    public RepositoryUtils(QueryExecutor executor, Loader<E> loader) {
        this.executor = executor;
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
            return executor.prepare(
                query,
                statement -> {
                    binder.bind(statement);

                    final ResultSet rs = statement.executeQuery();

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
     * Find list of entities from connection
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
            return executor.prepare(
                query,
                statement -> {
                    binder.bind(statement);

                    final ResultSet rs = statement.executeQuery();
                    final List<E> result = new ArrayList<>();

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
     * Find all entities from connection, without set prepared parameters
     *
     * util.findAll("SELECT * FROM ACCOUNT WHERE PSEUDO LIKE ?");
     *
     * @param query The find query
     *
     * @return The found entity
     *
     * @throws RepositoryException When error occurs during query execution
     *
     * @see RepositoryUtils#findOne(String, Binder) For find only one entity
     * @see Binder#NOP_BINDER
     */
    public List<E> findAll(String query) throws RepositoryException {
        return findAll(query, Binder.NOP_BINDER);
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
            return executor.prepare(
                query,
                statement -> {
                    binder.bind(statement);

                    final ResultSet rs = statement.executeQuery();

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
            return executor.prepare(
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
     * account.race(); // new race
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
            return executor.prepare(
                query,
                statement -> {
                    binder.bind(statement);
                    statement.execute();

                    final ResultSet rs = statement.getGeneratedKeys();

                    if (!rs.next()) {
                        throw new RepositoryException("No generated keys. Use RepositoryUtils#update(String, Binder) instead");
                    }

                    return loader.fillKeys(entity, rs);
                },
                true
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }
}
