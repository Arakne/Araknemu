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

import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.data.living.entity.account.Banishment;
import fr.quatrevieux.araknemu.data.living.repository.account.BanishmentRepository;
import fr.quatrevieux.araknemu.data.transformer.Transformer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

/**
 * SQL Implementation for {@link BanishmentRepository}
 */
final class SqlBanishmentRepository implements BanishmentRepository {
    private final QueryExecutor executor;
    private final RepositoryUtils<Banishment> utils;
    private final Transformer<Instant> instantTransformer;

    public SqlBanishmentRepository(QueryExecutor executor, Transformer<Instant> instantTransformer) {
        this.executor = executor;
        this.instantTransformer = instantTransformer;
        this.utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE BANISHMENT (" +
                    "BANISHMENT_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ACCOUNT_ID INTEGER," +
                    "START_DATE DATETIME," +
                    "END_DATE DATETIME," +
                    "CAUSE VARCHAR(255)," +
                    "BANISHER_ID INTEGER" +
                ")"
            );
            executor.query("CREATE INDEX IDX_IS_BANISHMENT ON BANISHMENT (ACCOUNT_ID, START_DATE, END_DATE)");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE BANISHMENT");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Banishment add(Banishment entity) {
        return utils.update(
            "INSERT INTO BANISHMENT (`ACCOUNT_ID`, `START_DATE`, `END_DATE`, `CAUSE`, `BANISHER_ID`) VALUES (?, ?, ?, ?, ?)",
            rs -> {
                rs.setInt(1, entity.accountId());
                rs.setString(2, instantTransformer.serialize(entity.startDate()));
                rs.setString(3, instantTransformer.serialize(entity.endDate()));
                rs.setString(4, entity.cause());
                rs.setInt(5, entity.banisherId());
            },
            entity
        );
    }

    @Override
    public void delete(Banishment entity) {
        utils.update("DELETE FROM BANISHMENT WHERE BANISHMENT_ID = ?", stmt -> stmt.setInt(1, entity.id()));
    }

    @Override
    public Banishment get(Banishment entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM BANISHMENT WHERE BANISHMENT_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        );
    }

    @Override
    public boolean has(Banishment entity) {
        return utils.aggregate(
            "SELECT COUNT(*) FROM BANISHMENT WHERE BANISHMENT_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public boolean isBanned(int accountId) {
        final String date = instantTransformer.serialize(Instant.now());

        return utils.aggregate(
            "SELECT COUNT(*) FROM BANISHMENT WHERE ACCOUNT_ID = ? AND START_DATE <= ? AND END_DATE >= ?",
            stmt -> {
                stmt.setInt(1, accountId);
                stmt.setString(2, date);
                stmt.setString(3, date);
            }
        ) > 0;
    }

    @Override
    public List<Banishment> forAccount(int accountId) {
        return utils.findAll(
            "SELECT * FROM BANISHMENT WHERE ACCOUNT_ID = ? ORDER BY START_DATE DESC",
            stmt -> stmt.setInt(1, accountId)
        );
    }

    @Override
    public void removeActive(int accountId) {
        final String date = instantTransformer.serialize(Instant.now());

        utils.update(
            "DELETE FROM BANISHMENT WHERE ACCOUNT_ID = ? AND START_DATE <= ? AND END_DATE >= ?",
            stmt -> {
                stmt.setInt(1, accountId);
                stmt.setString(2, date);
                stmt.setString(3, date);
            }
        );
    }

    private class Loader implements RepositoryUtils.Loader<Banishment> {
        @Override
        public Banishment create(ResultSet rs) throws SQLException {
            return new Banishment(
                rs.getInt("BANISHMENT_ID"),
                rs.getInt("ACCOUNT_ID"),
                instantTransformer.unserialize(rs.getString("START_DATE")),
                instantTransformer.unserialize(rs.getString("END_DATE")),
                rs.getString("CAUSE"),
                rs.getInt("BANISHER_ID")
            );
        }

        @Override
        public Banishment fillKeys(Banishment entity, ResultSet keys) throws SQLException {
            return entity.withId(keys.getInt(1));
        }
    }
}
