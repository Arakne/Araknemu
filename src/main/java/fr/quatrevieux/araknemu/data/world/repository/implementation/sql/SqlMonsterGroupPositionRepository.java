package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
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

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<MonsterGroupPosition> utils;

    public SqlMonsterGroupPositionRepository(ConnectionPool pool) {
        this.pool = new ConnectionPoolUtils(pool);

        utils = new RepositoryUtils<>(this.pool, new SqlMonsterGroupPositionRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
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
            pool.query("DROP TABLE MONSTER_GROUP_POSITION");
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
