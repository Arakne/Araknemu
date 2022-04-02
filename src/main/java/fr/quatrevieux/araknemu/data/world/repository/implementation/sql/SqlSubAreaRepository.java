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
import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.SubArea;
import fr.quatrevieux.araknemu.data.world.repository.environment.area.SubAreaRepository;
import fr.quatrevieux.araknemu.util.Asserter;
import org.checkerframework.checker.nullness.util.NullnessUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * SQL implementation for subarea repository
 */
final class SqlSubAreaRepository implements SubAreaRepository {
    private final QueryExecutor executor;
    private final RepositoryUtils<SubArea> utils;

    public SqlSubAreaRepository(QueryExecutor executor) {
        this.executor = executor;
        this.utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE SUBAREA (" +
                    "SUBAREA_ID INTEGER PRIMARY KEY," +
                    "AREA_ID INTEGER," +
                    "SUBAREA_NAME VARCHAR(200)," +
                    "CONQUESTABLE INTEGER(1)," +
                    "ALIGNMENT INTEGER(1)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE SUBAREA");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public SubArea get(SubArea entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public SubArea get(int id) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM SUBAREA WHERE SUBAREA_ID = ?",
            rs -> rs.setInt(1, id)
        );
    }

    @Override
    public boolean has(SubArea entity) {
        return utils.aggregate(
            "SELECT COUNT(*) FROM SUBAREA WHERE SUBAREA_ID = ?",
            rs -> rs.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<SubArea> all() {
        return utils.findAll("SELECT * FROM SUBAREA");
    }

    private static class Loader implements RepositoryUtils.Loader<SubArea> {
        @Override
        public SubArea create(ResultSet rs) throws SQLException {
            return new SubArea(
                rs.getInt("SUBAREA_ID"),
                rs.getInt("AREA_ID"),
                NullnessUtil.castNonNull(rs.getString("SUBAREA_NAME")),
                rs.getBoolean("CONQUESTABLE"),
                Alignment.byId(Asserter.assertGTENegativeOne(rs.getInt("ALIGNMENT")))
            );
        }

        @Override
        public SubArea fillKeys(SubArea entity, ResultSet keys) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }
}
