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
import fr.quatrevieux.araknemu.data.world.entity.environment.area.Area;
import fr.quatrevieux.araknemu.data.world.repository.environment.area.AreaRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * SQL implementation for subarea repository
 */
final class SqlAreaRepository implements AreaRepository {
    private static class Loader implements RepositoryUtils.Loader<Area> {
        @Override
        public Area create(ResultSet rs) throws SQLException {
            return new Area(
                rs.getInt("AREA_ID"),
                rs.getString("AREA_NAME"),
                rs.getInt("SUPERAREA_ID")
            );
        }

        @Override
        public Area fillKeys(Area entity, ResultSet keys) {
            throw new UnsupportedOperationException();
        }
    }

    final private QueryExecutor executor;
    final private RepositoryUtils<Area> utils;

    public SqlAreaRepository(QueryExecutor executor) {
        this.executor = executor;
        this.utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE AREA (" +
                    "AREA_ID INTEGER PRIMARY KEY," +
                    "AREA_NAME VARCHAR(200)," +
                    "SUPERAREA_ID INTEGER" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE AREA");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Area get(Area entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM AREA WHERE AREA_ID = ?",
            rs -> rs.setInt(1, entity.id())
        );
    }

    @Override
    public boolean has(Area entity) {
        return utils.aggregate(
            "SELECT COUNT(*) FROM AREA WHERE AREA_ID = ?",
            rs -> rs.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<Area> all() {
        return utils.findAll("SELECT * FROM AREA");
    }
}
