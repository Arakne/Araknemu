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
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository;
import org.checkerframework.checker.nullness.util.NullnessUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * SQL implementation for map triggers
 */
final class SqlMapTriggerRepository implements MapTriggerRepository {
    private final QueryExecutor executor;
    private final RepositoryUtils<MapTrigger> utils;

    public SqlMapTriggerRepository(QueryExecutor executor) {
        this.executor = executor;
        this.utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE MAP_TRIGGER (" +
                    "MAP_ID INTEGER," +
                    "CELL_ID INTEGER," +
                    "ACTION INTEGER," +
                    "ARGUMENTS TEXT," +
                    "CONDITIONS TEST," +
                    "PRIMARY KEY (MAP_ID, CELL_ID)" +
                ")"
            );

            executor.query("CREATE INDEX IDX_MAP ON MAP_TRIGGER (MAP_ID)");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE MAP_TRIGGER");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public MapTrigger get(MapTrigger entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM MAP_TRIGGER WHERE MAP_ID = ? AND CELL_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.map());
                stmt.setInt(2, entity.cell());
            }
        );
    }

    @Override
    public boolean has(MapTrigger entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM MAP_TRIGGER WHERE MAP_ID = ? AND CELL_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.map());
                stmt.setInt(2, entity.cell());
            }
        ) > 0;
    }

    @Override
    public Collection<MapTrigger> findByMap(int map) {
        return utils.findAll(
            "SELECT * FROM MAP_TRIGGER WHERE MAP_ID = ?",
            stmt -> stmt.setInt(1, map)
        );
    }

    @Override
    public Collection<MapTrigger> all() {
        return utils.findAll("SELECT * FROM MAP_TRIGGER");
    }

    private static class Loader implements RepositoryUtils.Loader<MapTrigger> {
        @Override
        public MapTrigger create(ResultSet rs) throws SQLException {
            return new MapTrigger(
                rs.getInt("MAP_ID"),
                rs.getInt("CELL_ID"),
                rs.getInt("ACTION"),
                NullnessUtil.castNonNull(rs.getString("ARGUMENTS")),
                NullnessUtil.castNonNull(rs.getString("CONDITIONS"))
            );
        }

        @Override
        public MapTrigger fillKeys(MapTrigger entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }
}
