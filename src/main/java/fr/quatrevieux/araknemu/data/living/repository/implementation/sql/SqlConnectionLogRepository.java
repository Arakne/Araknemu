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
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.data.living.entity.account.ConnectionLog;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.account.ConnectionLogRepository;
import fr.quatrevieux.araknemu.data.transformer.Transformer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.util.Optional;

/**
 * SQL implementation of {@link ConnectionLogRepository}
 */
final class SqlConnectionLogRepository implements ConnectionLogRepository {
    private class Loader implements RepositoryUtils.Loader<ConnectionLog> {
        @Override
        public ConnectionLog create(ResultSet rs) throws SQLException {
            ConnectionLog log = new ConnectionLog(
                rs.getInt("ACCOUNT_ID"),
                instantTransformer.unserialize(rs.getString("START_DATE")),
                rs.getString("IP_ADDRESS")
            );

            log.setEndDate(instantTransformer.unserialize(rs.getString("END_DATE")));
            log.setClientUid(rs.getString("CLIENT_UID"));

            int serverId = rs.getInt("SERVER_ID");

            if (!rs.wasNull()) {
                log.setServerId(serverId);
            }

            int playerId = rs.getInt("PLAYER_ID");

            if (!rs.wasNull()) {
                log.setPlayerId(playerId);
            }

            return log;
        }

        @Override
        public ConnectionLog fillKeys(ConnectionLog entity, ResultSet keys) {
            throw new UnsupportedOperationException();
        }
    }

    final private QueryExecutor executor;
    final private RepositoryUtils<ConnectionLog> utils;
    final private Transformer<Instant> instantTransformer;

    public SqlConnectionLogRepository(QueryExecutor executor, Transformer<Instant> instantTransformer) {
        this.executor = executor;
        this.instantTransformer = instantTransformer;
        this.utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE CONNECTION_LOG (" +
                    "ACCOUNT_ID INTEGER NOT NULL," +
                    "START_DATE DATETIME NOT NULL," +
                    "IP_ADDRESS VARCHAR(45) NOT NULL," +
                    "END_DATE DATETIME," +
                    "SERVER_ID INTEGER," +
                    "PLAYER_ID INTEGER," +
                    "CLIENT_UID VARCHAR(32)," +
                    "PRIMARY KEY (ACCOUNT_ID, START_DATE)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE CONNECTION_LOG");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public ConnectionLog add(ConnectionLog entity) {
        utils.update(
            "REPLACE INTO CONNECTION_LOG (`ACCOUNT_ID`, `START_DATE`, `IP_ADDRESS`) VALUES (?, ?, ?)",
            rs -> {
                rs.setInt(1, entity.accountId());
                rs.setString(2, instantTransformer.serialize(entity.startDate()));
                rs.setString(3, entity.ipAddress());
            }
        );

        return entity;
    }

    @Override
    public void delete(ConnectionLog entity) {
        utils.update("DELETE FROM CONNECTION_LOG WHERE ACCOUNT_ID = ? AND START_DATE = ?", rs -> {
            rs.setInt(1, entity.accountId());
            rs.setString(2, instantTransformer.serialize(entity.startDate()));
        });
    }

    @Override
    public ConnectionLog get(ConnectionLog entity) throws RepositoryException {
        return utils.findOne("SELECT * FROM CONNECTION_LOG WHERE ACCOUNT_ID = ? AND START_DATE = ?", rs -> {
            rs.setInt(1, entity.accountId());
            rs.setString(2, instantTransformer.serialize(entity.startDate()));
        });
    }

    @Override
    public boolean has(ConnectionLog entity) {
        return utils.aggregate("SELECT COUNT(*) FROM CONNECTION_LOG WHERE ACCOUNT_ID = ? AND START_DATE = ?", rs -> {
            rs.setInt(1, entity.accountId());
            rs.setString(2, instantTransformer.serialize(entity.startDate()));
        }) > 0;
    }

    @Override
    public Optional<ConnectionLog> lastSession(int accountId) {
        try {
            return Optional.of(utils.findOne(
                "SELECT * FROM CONNECTION_LOG WHERE ACCOUNT_ID = ? AND END_DATE IS NOT NULL ORDER BY START_DATE DESC LIMIT 1",
                rs -> rs.setInt(1, accountId)
            ));
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public ConnectionLog currentSession(int accountId) {
        return utils.findOne(
            "SELECT * FROM CONNECTION_LOG WHERE ACCOUNT_ID = ? AND END_DATE IS NULL ORDER BY START_DATE DESC LIMIT 1",
            rs -> rs.setInt(1, accountId)
        );
    }

    @Override
    public void save(ConnectionLog log) {
        utils.update(
            "UPDATE CONNECTION_LOG SET `SERVER_ID` = ?, `PLAYER_ID` = ?, `END_DATE` = ?, `CLIENT_UID` = ? WHERE `ACCOUNT_ID` = ? AND `START_DATE` = ?",
            rs -> {
                if (log.serverId() != null) {
                    rs.setInt(1, log.serverId());
                } else {
                    rs.setNull(1, Types.INTEGER);
                }

                if (log.playerId() != null) {
                    rs.setInt(2, log.playerId());
                } else {
                    rs.setNull(2, Types.INTEGER);
                }

                rs.setString(3, instantTransformer.serialize(log.endDate()));
                rs.setString(4, log.clientUid());
                rs.setInt(5, log.accountId());
                rs.setString(6, instantTransformer.serialize(log.startDate()));
            }
        );
    }

    @Override
    public boolean hasAlreadyPlayed(Player player) {
        return utils.aggregate(
            "SELECT COUNT(*) FROM CONNECTION_LOG WHERE PLAYER_ID = ? AND END_DATE IS NOT NULL",
            stmt -> stmt.setInt(1, player.id())
        ) > 0;
    }
}
