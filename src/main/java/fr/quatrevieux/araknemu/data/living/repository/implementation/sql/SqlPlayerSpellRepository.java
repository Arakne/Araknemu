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
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * SQL implementation for {@link PlayerSpell} repository
 */
final class SqlPlayerSpellRepository implements PlayerSpellRepository {
    private class Loader implements RepositoryUtils.Loader<PlayerSpell> {
        @Override
        public PlayerSpell create(ResultSet rs) throws SQLException {
            return new PlayerSpell(
                rs.getInt("PLAYER_ID"),
                rs.getInt("SPELL_ID"),
                rs.getBoolean("CLASS_SPELL"),
                rs.getInt("SPELL_LEVEL"),
                rs.getInt("SPELL_POSITION")
            );
        }

        @Override
        public PlayerSpell fillKeys(PlayerSpell entity, ResultSet keys) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }

    final private QueryExecutor executor;
    final private RepositoryUtils<PlayerSpell> utils;

    public SqlPlayerSpellRepository(QueryExecutor executor) {
        this.executor = executor;
        this.utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE PLAYER_SPELL (" +
                    "PLAYER_ID INTEGER," +
                    "SPELL_ID INTEGER," +
                    "CLASS_SPELL BOOLEAN," +
                    "SPELL_LEVEL TINYINT," +
                    "SPELL_POSITION TINYINT," +
                    "PRIMARY KEY (PLAYER_ID, SPELL_ID)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE PLAYER_SPELL");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Collection<PlayerSpell> byPlayer(Player player) {
        return utils.findAll(
            "SELECT * FROM PLAYER_SPELL WHERE PLAYER_ID = ?",
            stmt -> stmt.setInt(1, player.id())
        );
    }

    @Override
    public void delete(PlayerSpell item) {
        int count = utils.update(
            "DELETE FROM PLAYER_SPELL WHERE PLAYER_ID = ? AND SPELL_ID = ?",
            stmt -> {
                stmt.setInt(1, item.playerId());
                stmt.setInt(2, item.spellId());
            }
        );

        if (count != 1) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public PlayerSpell add(PlayerSpell entity) throws RepositoryException {
        utils.update(
            "REPLACE INTO PLAYER_SPELL (PLAYER_ID, SPELL_ID, CLASS_SPELL, SPELL_LEVEL, SPELL_POSITION) VALUES (?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setInt(1,     entity.playerId());
                stmt.setInt(2,     entity.spellId());
                stmt.setBoolean(3, entity.classSpell());
                stmt.setInt(4,     entity.level());
                stmt.setInt(5,     entity.position());
            }
        );

        return entity;
    }

    @Override
    public PlayerSpell get(PlayerSpell entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM PLAYER_SPELL WHERE PLAYER_ID = ? AND SPELL_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.playerId());
                stmt.setInt(2, entity.spellId());
            }
        );
    }

    @Override
    public boolean has(PlayerSpell entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM PLAYER_SPELL WHERE PLAYER_ID = ? AND SPELL_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.playerId());
                stmt.setInt(2, entity.spellId());
            }
        ) > 0;
    }
}
