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
import fr.quatrevieux.araknemu.data.living.entity.BanIp;
import fr.quatrevieux.araknemu.data.living.repository.BanIpRepository;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import inet.ipaddr.IPAddressString;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;

/**
 * SQL Implementation for {@link BanIpRepository}
 */
final class SqlBanIpRepository implements BanIpRepository {
    private class Loader implements RepositoryUtils.Loader<BanIp> {
        @Override
        public BanIp create(ResultSet rs) throws SQLException {
            return new BanIp(
                rs.getInt("BANIP_ID"),
                ipAddressTransformer.unserialize(rs.getString("IP_ADDRESS")),
                instantTransformer.unserialize(rs.getString("UPDATED_AT")),
                instantTransformer.unserialize(rs.getString("EXPIRES_AT")),
                rs.getString("CAUSE"),
                rs.getInt("BANISHER_ID")
            );
        }

        @Override
        public BanIp fillKeys(BanIp entity, ResultSet keys) throws SQLException {
            return entity.withId(keys.getInt(1));
        }
    }

    final private QueryExecutor executor;
    final private RepositoryUtils<BanIp> utils;
    final private Transformer<Instant> instantTransformer;
    final private Transformer<IPAddressString> ipAddressTransformer;

    public SqlBanIpRepository(QueryExecutor executor, Transformer<IPAddressString> ipAddressTransformer, Transformer<Instant> instantTransformer) {
        this.executor = executor;
        this.ipAddressTransformer = ipAddressTransformer;
        this.instantTransformer = instantTransformer;
        this.utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE BANIP (" +
                    "BANIP_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "IP_ADDRESS VARCHAR(64)," +
                    "UPDATED_AT DATETIME," +
                    "EXPIRES_AT DATETIME," +
                    "CAUSE VARCHAR(256)," +
                    "BANISHER_ID INTEGER" +
                ")"
            );
            executor.query("CREATE INDEX IDX_EXPIRES_AT ON BANIP (EXPIRES_AT)");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE BANIP");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public BanIp add(BanIp entity) {
        return utils.update(
            "INSERT INTO BANIP (`IP_ADDRESS`, `UPDATED_AT`, `EXPIRES_AT`, `CAUSE`, `BANISHER_ID`) VALUES (?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setString(1, ipAddressTransformer.serialize(entity.ipAddress()));
                stmt.setString(2, instantTransformer.serialize(entity.updatedAt()));
                stmt.setString(3, entity.expiresAt().map(instantTransformer::serialize).orElse(null));
                stmt.setString(4, entity.cause());
                stmt.setInt(5, entity.banisherId());
            },
            entity
        );
    }

    @Override
    public void delete(BanIp entity) {
        utils.update("DELETE FROM BANIP WHERE IP_ADDRESS = ?", stmt -> stmt.setString(1, ipAddressTransformer.serialize(entity.ipAddress())));
    }

    @Override
    public BanIp get(BanIp entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM BANIP WHERE BANIP_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        );
    }

    @Override
    public boolean has(BanIp entity) {
        return utils.aggregate(
            "SELECT COUNT(*) FROM BANIP WHERE BANIP_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<BanIp> available() {
        return utils.findAll(
            "SELECT * FROM BANIP WHERE EXPIRES_AT > ? OR EXPIRES_AT IS NULL",
            stmt -> stmt.setString(1, instantTransformer.serialize(Instant.now()))
        );
    }

    @Override
    public Collection<BanIp> updated(Instant after) {
        return utils.findAll(
            "SELECT * FROM BANIP WHERE UPDATED_AT >= ?",
            stmt -> stmt.setString(1, instantTransformer.serialize(after))
        );
    }

    @Override
    public void disable(IPAddressString ipAddress) {
        final String now = instantTransformer.serialize(Instant.now());

        utils.update(
            "UPDATE BANIP SET UPDATED_AT = ?, EXPIRES_AT = ? WHERE IP_ADDRESS = ? AND (EXPIRES_AT >= ? OR EXPIRES_AT IS NULL)",
            stmt -> {
                stmt.setString(1, now);
                stmt.setString(2, now);
                stmt.setString(3, ipAddressTransformer.serialize(ipAddress));
                stmt.setString(4, now);
            }
        );
    }
}
