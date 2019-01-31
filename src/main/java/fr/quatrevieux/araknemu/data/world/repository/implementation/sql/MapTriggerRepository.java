package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * SQL implementation for map triggers
 */
final class MapTriggerRepository implements fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository {
    private static class Loader implements RepositoryUtils.Loader<MapTrigger> {
        @Override
        public MapTrigger create(ResultSet rs) throws SQLException {
            return new MapTrigger(
                rs.getInt("MAP_ID"),
                rs.getInt("CELL_ID"),
                rs.getInt("ACTION"),
                rs.getString("ARGUMENTS"),
                rs.getString("CONDITIONS")
            );
        }

        @Override
        public MapTrigger fillKeys(MapTrigger entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<MapTrigger> utils;

    public MapTriggerRepository(ConnectionPool pool) {
        this.pool = new ConnectionPoolUtils(pool);
        this.utils = new RepositoryUtils<>(this.pool, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE MAP_TRIGGER (" +
                    "MAP_ID INTEGER," +
                    "CELL_ID INTEGER," +
                    "ACTION INTEGER," +
                    "ARGUMENTS TEXT," +
                    "CONDITIONS TEST," +
                    "PRIMARY KEY (MAP_ID, CELL_ID)" +
                ")"
            );

            pool.query("CREATE INDEX IDX_MAP ON MAP_TRIGGER (MAP_ID)");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE MAP_TRIGGER");
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
}
