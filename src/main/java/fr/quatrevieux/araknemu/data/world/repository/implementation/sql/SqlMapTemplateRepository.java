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

package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.arakne.utils.maps.serializer.CellData;
import fr.arakne.utils.value.Dimensions;
import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.Record;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import org.checkerframework.checker.index.qual.NonNegative;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Map repository implementation for SQL database
 */
final class SqlMapTemplateRepository implements MapTemplateRepository {
    private final QueryExecutor executor;
    private final RepositoryUtils<MapTemplate> utils;
    private final Transformer<CellData[]> cellsTransformer;
    private final Transformer<@NonNegative int[][]> fightPlacesTransformer;

    public SqlMapTemplateRepository(QueryExecutor executor, Transformer<CellData[]> cellsTransformer, Transformer<@NonNegative int[][]> fightPlacesTransformer) {
        this.executor = executor;
        this.cellsTransformer = cellsTransformer;
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
                    "SUBAREA_ID INTEGER," +
                    "INDOOR BOOLEAN" +
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
    public MapTemplate get(@NonNegative int id) {
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
    public Collection<MapTemplate> byGeolocation(Geolocation geolocation) {
        return utils.findAll("SELECT * FROM maps WHERE MAP_X = ? AND MAP_Y = ?", stmt -> {
            stmt.setInt(1, geolocation.x());
            stmt.setInt(2, geolocation.y());
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

    private class Loader implements RepositoryUtils.Loader<MapTemplate> {
        @Override
        public MapTemplate create(Record record) throws SQLException {
            return new MapTemplate(
                record.getNonNegativeInt("id"),
                record.getString("date"),
                new Dimensions(
                    record.getPositiveInt("width"),
                    record.getPositiveInt("height")
                ),
                record.getString("key"),
                record.unserialize("mapData", cellsTransformer),
                record.unserialize("places", fightPlacesTransformer),
                new Geolocation(
                    record.getInt("MAP_X"),
                    record.getInt("MAP_Y")
                ),
                record.getInt("SUBAREA_ID"),
                record.getBoolean("INDOOR")
            );
        }

        @Override
        public MapTemplate fillKeys(MapTemplate entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }
}
