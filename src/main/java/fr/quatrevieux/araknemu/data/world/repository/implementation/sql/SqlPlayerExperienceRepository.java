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

package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerExperience;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerExperienceRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * SQL implementation of the repository
 */
final class SqlPlayerExperienceRepository implements PlayerExperienceRepository {
    private class Loader implements RepositoryUtils.Loader<PlayerExperience> {
        @Override
        public PlayerExperience create(ResultSet rs) throws SQLException {
            return new PlayerExperience(
                rs.getInt("PLAYER_LEVEL"),
                rs.getLong("EXPERIENCE")
            );
        }

        @Override
        public PlayerExperience fillKeys(PlayerExperience entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private QueryExecutor executor;
    final private RepositoryUtils<PlayerExperience> utils;

    public SqlPlayerExperienceRepository(QueryExecutor executor) {
        this.executor = executor;
        utils = new RepositoryUtils<>(executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE PLAYER_XP (" +
                    "PLAYER_LEVEL SMALLINT PRIMARY KEY," +
                    "EXPERIENCE BIGINT" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE PLAYER_XP");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public PlayerExperience get(PlayerExperience entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM PLAYER_XP WHERE PLAYER_LEVEL = ?",
            stmt -> stmt.setInt(1, entity.level())
        );
    }

    @Override
    public boolean has(PlayerExperience entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM PLAYER_XP WHERE PLAYER_LEVEL = ?",
            stmt -> stmt.setInt(1, entity.level())
        ) > 0;
    }

    @Override
    public List<PlayerExperience> all() {
        return utils.findAll("SELECT * FROM PLAYER_XP ORDER BY PLAYER_LEVEL ASC");
    }
}
