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
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.Dimensions;
import fr.quatrevieux.araknemu.data.value.Geoposition;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Map repository implementation for SQL database
 */
final class SqlMapTemplateRepository implements MapTemplateRepository {
    private class Loader implements RepositoryUtils.Loader<MapTemplate> {
        @Override
        public MapTemplate create(ResultSet rs) throws SQLException {
            String cellsData = rs.getString("mapData");

            List<MapTemplate.Cell> cells = new ArrayList<>(cellsData.length() / 10);

            for (int i = 0; i < cellsData.length(); i += 10) {
                cells.add(
                    cellTransformer.unserialize(cellsData.substring(i, i + 10))
                );
            }

            return new MapTemplate(
                rs.getInt("id"),
                rs.getString("date"),
                new Dimensions(
                    rs.getInt("width"),
                    rs.getInt("height")
                ),
                rs.getString("key"),
                cells,
                fightPlacesTransformer.unserialize(rs.getString("places")),
                new Geoposition(
                    rs.getInt("MAP_X"),
                    rs.getInt("MAP_Y")
                ),
                rs.getInt("SUBAREA_ID")
            );
        }

        @Override
        public MapTemplate fillKeys(MapTemplate entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private QueryExecutor executor;
    final private RepositoryUtils<MapTemplate> utils;
    final private Transformer<MapTemplate.Cell> cellTransformer;
    final private Transformer<List<Integer>[]> fightPlacesTransformer;

    public SqlMapTemplateRepository(QueryExecutor executor, Transformer<MapTemplate.Cell> cellTransformer, Transformer<List<Integer>[]> fightPlacesTransformer) {
        this.executor = executor;
        this.cellTransformer = cellTransformer;
        this.fightPlacesTransformer = fightPlacesTransformer;

        utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            // @todo normalize names
            executor.query(
                "CREATE TABLE maps(" +
                    "id INTEGER PRIMARY KEY," +
                    "date VARCHAR(12)," +
                    "width INTEGER," +
                    "height INTEGER," +
                    "key TEXT," +
                    "mapData TEXT," +
                    "places TEXT," +
                    "MAP_X INTEGER," +
                    "MAP_Y INTEGER," +
                    "SUBAREA_ID INTEGER" +
                ")"
            );

            executor.query("CREATE INDEX IDX_MAP_POS ON maps (MAP_X, MAP_Y)");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE maps");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public MapTemplate get(int id) {
        return utils.findOne(
            "SELECT * FROM maps WHERE id = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public MapTemplate get(MapTemplate entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public Collection<MapTemplate> byGeoposition(Geoposition geoposition) {
        return utils.findAll("SELECT * FROM maps WHERE MAP_X = ? AND MAP_Y = ?", stmt -> {
            stmt.setInt(1, geoposition.x());
            stmt.setInt(2, geoposition.y());
        });
    }

    @Override
    public boolean has(MapTemplate entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM maps WHERE id = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<MapTemplate> all() throws RepositoryException {
        return utils.findAll("SELECT * FROM maps");
    }
}
