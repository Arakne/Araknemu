package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.Dimensions;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Map repository implementation for SQL database
 */
final class MapTemplateRepository implements fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository {
    private static class Loader implements RepositoryUtils.Loader<MapTemplate> {
        final private Transformer<MapTemplate.Cell> cellTransformer;

        public Loader(Transformer<MapTemplate.Cell> cellTransformer) {
            this.cellTransformer = cellTransformer;
        }

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
                cells
            );
        }

        @Override
        public MapTemplate fillKeys(MapTemplate entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<MapTemplate> utils;

    public MapTemplateRepository(ConnectionPool pool, Transformer<MapTemplate.Cell> cellTransformer) {
        this.pool = new ConnectionPoolUtils(pool);
        utils = new RepositoryUtils<>(this.pool, new Loader(cellTransformer));
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE maps(" +
                    "id INTEGER PRIMARY KEY," +
                    "date VARCHAR(12)," +
                    "width INTEGER," +
                    "height INTEGER," +
                    "key TEXT," +
                    "mapData TEXT" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE maps");
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
    public boolean has(MapTemplate entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM maps WHERE id = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<MapTemplate> all() throws RepositoryException {
        return utils.findAll(
            "SELECT * FROM maps",
            rs -> {}
        );
    }
}
