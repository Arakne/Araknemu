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

import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupPositionRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * SQL implementation for monster group position repository
 */
final class SqlMonsterGroupPositionRepository implements MonsterGroupPositionRepository {
    private class Loader implements RepositoryUtils.Loader<MonsterGroupPosition> {
        @Override
        public MonsterGroupPosition create(ResultSet rs) throws SQLException {
            return new MonsterGroupPosition(
                new Position(
                    rs.getInt("MAP_ID"),
                    rs.getInt("CELL_ID")
                ),
                rs.getInt("MONSTER_GROUP_ID")
            );
        }

        @Override
        public MonsterGroupPosition fillKeys(MonsterGroupPosition entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    private final QueryExecutor executor;
    private final RepositoryUtils<MonsterGroupPosition> utils;

    public SqlMonsterGroupPositionRepository(QueryExecutor executor) {
        this.executor = executor;

        utils = new RepositoryUtils<>(this.executor, new SqlMonsterGroupPositionRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE `MONSTER_GROUP_POSITION` (" +
                    "`MAP_ID` INTEGER," +
                    "`CELL_ID` INTEGER," +
                    "`MONSTER_GROUP_ID` INTEGER," +
                    "PRIMARY KEY (`MAP_ID`, `CELL_ID`)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE MONSTER_GROUP_POSITION");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public MonsterGroupPosition get(MonsterGroupPosition entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM MONSTER_GROUP_POSITION WHERE MAP_ID = ? AND CELL_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.position().map());
                stmt.setInt(2, entity.position().cell());
            }
        );
    }

    @Override
    public boolean has(MonsterGroupPosition entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM MONSTER_GROUP_POSITION WHERE MAP_ID = ? AND CELL_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.position().map());
                stmt.setInt(2, entity.position().cell());
            }
        ) > 0;
    }

    @Override
    public Collection<MonsterGroupPosition> byMap(int mapId) {
        return utils.findAll(
            "SELECT * FROM MONSTER_GROUP_POSITION WHERE MAP_ID = ?",
            stmt -> stmt.setInt(1, mapId)
        );
    }

    @Override
    public Collection<MonsterGroupPosition> all() {
        return utils.findAll("SELECT * FROM MONSTER_GROUP_POSITION");
    }
}
