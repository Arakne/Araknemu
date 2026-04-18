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
 * Copyright (c) 2017-2026 Leor Finacre
 */
package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.Record;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.social.Friend;
import fr.quatrevieux.araknemu.data.living.repository.social.friend.FriendRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.handler.social.exception.FriendException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

final class SqlFriendRepository implements FriendRepository {

    private final QueryExecutor executor;
    private final RepositoryUtils<Friend> utils;
    private final GameConfiguration configuration;

    public SqlFriendRepository(QueryExecutor executor, GameConfiguration configuration) {
        this.executor = executor;
        this.configuration = configuration;
        this.utils = new RepositoryUtils<>(this.executor, new SqlFriendRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                    "CREATE TABLE FRIEND (" +
                            "ACCOUNT_ID INTEGER," +
                            "SERVER_ID INTEGER," +
                            "CONTACT_ID INTEGER," +
                            "IS_ENEMY TINY_INT" +
                            "UNIQUE KEY (ACCOUNT_ID, SERVER_ID, CONTACT_ID)" +
                            ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE FRIEND");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Friend get(Friend entity) throws RepositoryException {
        try {
            return utils.findOne("SELECT * FROM FRIEND WHERE ACCOUNT_ID = ? AND SERVER_ID = ?", rs -> {
                rs.setInt(1, entity.accountId());
                rs.setInt(2, entity.serverId());
            });
        } catch (EntityNotFoundException e) {
            return entity;
        }
    }

    @Override
    public Friend add(GameAccount account, Friend entity, boolean isEnemy) throws FriendException {
        if(this.getAll(account, isEnemy).size() >= configuration.maxFriends()) {
            throw new FriendException("Too many friends");
        }

        utils.update(
                "REPLACE INTO FRIEND (`ACCOUNT_ID`, `SERVER_ID`, `CONTACT_ID`, `IS_ENEMY`) VALUES (?, ?, ?, ?)",
                rs -> {
                    rs.setInt(1, entity.accountId());
                    rs.setInt(2, entity.serverId());
                    rs.setInt(3, entity.contactId());
                    rs.setBoolean(4, entity.isEnemy());
                }
        );

        return entity;
    }

    @Override
    public Friend add(Friend entity) throws RepositoryException {
        utils.update(
                "REPLACE INTO FRIEND (`ACCOUNT_ID`, `SERVER_ID`, `CONTACT_ID`, `IS_ENEMY`) VALUES (?, ?, ?, ?)",
                rs -> {
                    rs.setInt(1, entity.accountId());
                    rs.setInt(2, entity.serverId());
                    rs.setInt(3, entity.contactId());
                    rs.setBoolean(4, entity.isEnemy());
                }
        );

        return entity;
    }

    @Override
    public void delete(Friend entity) throws RepositoryException {
        final int count = utils.update(
        "DELETE FROM FRIEND WHERE ACCOUNT_ID = ? AND SERVER_ID = ? AND CONTACT_ID = ?", rs -> {
            rs.setInt(1, entity.accountId());
            rs.setInt(2, entity.serverId());
            rs.setInt(3, entity.contactId());
        });

        if (count != 1) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public Collection<Friend> getAll(GameAccount entity, boolean isEnemy) {
        return utils.findAll("SELECT * FROM FRIEND WHERE ACCOUNT_ID = ? AND SERVER_ID = ? AND IS_ENEMY = ?", rs -> {
            rs.setInt(1, entity.id());
            rs.setInt(2, entity.serverId());
            rs.setBoolean(3, isEnemy);
        });
    }

    @Override
    public boolean has(Friend entity) throws RepositoryException {
        return utils.aggregate("SELECT COUNT(*) FROM FRIEND WHERE ACCOUNT_ID = ? AND SERVER_ID = ? AND CONTACT_ID = ?", rs -> {
            rs.setInt(1, entity.accountId());
            rs.setInt(2, entity.serverId());
            rs.setInt(3, entity.contactId());
        }) > 0;
    }

    private static class Loader implements RepositoryUtils.Loader<Friend> {
        @Override
        public Friend create(Record record) throws SQLException {
            return new Friend(
                    record.getInt("ACCOUNT_ID"),
                    record.getInt("SERVER_ID"),
                    record.getInt("CONTACT_Id"),
                    record.getBoolean("IS_ENEMY")
            );
        }

        @Override
        public Friend fillKeys(Friend entity, ResultSet keys) {
            throw new UnsupportedOperationException();
        }
    }
}
