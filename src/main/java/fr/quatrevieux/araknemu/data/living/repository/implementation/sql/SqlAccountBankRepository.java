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

package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.Record;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountBankRepository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQL implementation of {@link AccountBankRepository}
 */
final class SqlAccountBankRepository implements AccountBankRepository {
    private final QueryExecutor executor;
    private final RepositoryUtils<AccountBank> utils;

    public SqlAccountBankRepository(QueryExecutor executor) {
        this.executor = executor;
        this.utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE BANK (" +
                    "ACCOUNT_ID INTEGER," +
                    "SERVER_ID INTEGER," +
                    "BANK_KAMAS BIGINT," +
                    "PRIMARY KEY (ACCOUNT_ID, SERVER_ID)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE BANK");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public AccountBank add(AccountBank entity) {
        utils.update(
            "REPLACE INTO BANK (`ACCOUNT_ID`, `SERVER_ID`, `BANK_KAMAS`) VALUES (?, ?, ?)",
            rs -> {
                rs.setInt(1, entity.accountId());
                rs.setInt(2, entity.serverId());
                rs.setLong(3, entity.kamas());
            }
        );

        return entity;
    }

    @Override
    public void delete(AccountBank entity) {
        utils.update("DELETE FROM BANK WHERE ACCOUNT_ID = ? AND SERVER_ID = ?", rs -> {
            rs.setInt(1, entity.accountId());
            rs.setInt(2, entity.serverId());
        });
    }

    @Override
    public AccountBank get(AccountBank entity) throws RepositoryException {
        try {
            return utils.findOne("SELECT * FROM BANK WHERE ACCOUNT_ID = ? AND SERVER_ID = ?", rs -> {
                rs.setInt(1, entity.accountId());
                rs.setInt(2, entity.serverId());
            });
        } catch (EntityNotFoundException e) {
            entity.setKamas(0); // Ensure that kamas are set to zero

            return entity;
        }
    }

    @Override
    public boolean has(AccountBank entity) {
        return utils.aggregate("SELECT COUNT(*) FROM BANK WHERE ACCOUNT_ID = ? AND SERVER_ID = ?", rs -> {
            rs.setInt(1, entity.accountId());
            rs.setInt(2, entity.serverId());
        }) > 0;
    }

    private static class Loader implements RepositoryUtils.Loader<AccountBank> {
        @Override
        public AccountBank create(Record record) throws SQLException {
            return new AccountBank(
                record.getInt("ACCOUNT_ID"),
                record.getInt("SERVER_ID"),
                record.getNonNegativeLong("BANK_KAMAS")
            );
        }

        @Override
        public AccountBank fillKeys(AccountBank entity, ResultSet keys) {
            throw new UnsupportedOperationException();
        }
    }
}
