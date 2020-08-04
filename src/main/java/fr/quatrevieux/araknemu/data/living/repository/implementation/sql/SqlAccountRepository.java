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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.living.transformer.PermissionsTransformer;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

final class SqlAccountRepository implements AccountRepository {
    private static class Loader implements RepositoryUtils.Loader<Account> {
        final private PermissionsTransformer permissionsTransformer;

        public Loader(PermissionsTransformer permissionsTransformer) {
            this.permissionsTransformer = permissionsTransformer;
        }

        @Override
        public Account create(ResultSet rs) throws SQLException {
            return new Account(
                rs.getInt("ACCOUNT_ID"),
                rs.getString("USERNAME"),
                rs.getString("PASSWORD"),
                rs.getString("PSEUDO"),
                permissionsTransformer.unserialize(rs.getInt("PERMISSIONS")),
                rs.getString("QUESTION"),
                rs.getString("ANSWER")
            );
        }

        @Override
        public Account fillKeys(Account entity, ResultSet keys) throws SQLException {
            return entity.withId(
                keys.getInt(1)
            );
        }
    }

    final private QueryExecutor executor;
    final private RepositoryUtils<Account> utils;
    final private PermissionsTransformer permissionsTransformer;

    public SqlAccountRepository(QueryExecutor executor, PermissionsTransformer permissionsTransformer) {
        this.executor = executor;
        this.utils = new RepositoryUtils<>(this.executor, new Loader(permissionsTransformer));
        this.permissionsTransformer = permissionsTransformer;
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE ACCOUNT (" +
                    "ACCOUNT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "USERNAME VARCHAR(32) UNIQUE," +
                    "PASSWORD VARCHAR(256)," +
                    "PSEUDO VARCHAR(32) UNIQUE," +
                    "PERMISSIONS INTEGER," +
                    "QUESTION VARCHAR(64)," +
                    "ANSWER VARCHAR(255)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE ACCOUNT");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Account add(Account entity) {
        return utils.update(
            "INSERT INTO ACCOUNT (`USERNAME`, `PASSWORD`, `PSEUDO`, `PERMISSIONS`, `QUESTION`, `ANSWER`) VALUES (?, ?, ?, ?, ?, ?)",
            rs -> {
                rs.setString(1, entity.name());
                rs.setString(2, entity.password());
                rs.setString(3, entity.pseudo());
                rs.setInt(4,    permissionsTransformer.serialize(entity.permissions()));
                rs.setString(5, entity.question());
                rs.setString(6, entity.answer());
            },
            entity
        );
    }

    @Override
    public void delete(Account entity) {
        utils.update("DELETE FROM ACCOUNT WHERE ACCOUNT_ID = ?", rs -> rs.setInt(1, entity.id()));
    }

    @Override
    public Account get(Account entity) throws RepositoryException {
        return get(entity.id());
    }

    // @fixme not on the interface
    public Account get(int id) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM ACCOUNT WHERE ACCOUNT_ID = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public boolean has(Account entity) {
        return utils.aggregate(
            "SELECT COUNT(*) FROM ACCOUNT WHERE ACCOUNT_ID = ?",
            rs -> rs.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Account findByUsername(String username) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM ACCOUNT WHERE USERNAME = ?",
            rs -> rs.setString(1, username)
        );
    }

    @Override
    public void savePassword(Account entity) {
        utils.update("UPDATE ACCOUNT SET PASSWORD = ? WHERE ACCOUNT_ID = ?", stmt -> {
            stmt.setString(1, entity.password());
            stmt.setInt(2, entity.id());
        });
    }

    @Override
    public Collection<Account> findByIds(int[] ids) {
        if (ids.length == 0) {
            return Collections.emptyList();
        }

        if (ids.length == 1) {
            return Collections.singleton(get(ids[0]));
        }

        return utils.findAll(
            "SELECT * FROM ACCOUNT WHERE ACCOUNT_ID IN (" + StringUtils.repeat("?, ", ids.length - 1) + "?)",
            stmt -> {
                for (int i = 0; i < ids.length; ++i) {
                    stmt.setInt(i + 1, ids[i]);
                }
            }
        );
    }
}
