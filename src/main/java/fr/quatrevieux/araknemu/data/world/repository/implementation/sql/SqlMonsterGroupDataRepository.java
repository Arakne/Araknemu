package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

/**
 * SQL implementation for monster group repository
 */
final class SqlMonsterGroupDataRepository implements MonsterGroupDataRepository {
    private class Loader implements RepositoryUtils.Loader<MonsterGroupData> {
        @Override
        public MonsterGroupData create(ResultSet rs) throws SQLException {
            return new MonsterGroupData(
                rs.getInt("MONSTER_GROUP_ID"),
                Duration.ofMillis(rs.getLong("RESPAWN_TIME")),
                rs.getInt("MAX_SIZE"),
                rs.getInt("MAX_COUNT"),
                monstersTransformer.unserialize(rs.getString("MONSTERS")),
                rs.getString("COMMENT"),
                new Position(
                    rs.getInt("WIN_FIGHT_TELEPORT_MAP_ID"),
                    rs.getInt("WIN_FIGHT_TELEPORT_CELL_ID")
                ),
                rs.getBoolean("FIXED_TEAM_NUMBER")
            );
        }

        @Override
        public MonsterGroupData fillKeys(MonsterGroupData entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<MonsterGroupData> utils;
    final private Transformer<List<MonsterGroupData.Monster>> monstersTransformer;

    public SqlMonsterGroupDataRepository(ConnectionPool pool, Transformer<List<MonsterGroupData.Monster>> monstersTransformer) {
        this.pool = new ConnectionPoolUtils(pool);
        this.monstersTransformer = monstersTransformer;

        utils = new RepositoryUtils<>(this.pool, new SqlMonsterGroupDataRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE `MONSTER_GROUP` (" +
                    "`MONSTER_GROUP_ID` INTEGER PRIMARY KEY," +
                    "`RESPAWN_TIME` INTEGER," +
                    "`MAX_SIZE` INTEGER," +
                    "`MAX_COUNT` INTEGER," +
                    "`MONSTERS` TEXT," +
                    "`COMMENT` VARCHAR(24)," +
                    "`WIN_FIGHT_TELEPORT_MAP_ID` INTEGER," +
                    "`WIN_FIGHT_TELEPORT_CELL_ID` INTEGER," +
                    "`FIXED_TEAM_NUMBER` INTEGER" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE MONSTER_GROUP");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public MonsterGroupData get(MonsterGroupData entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public MonsterGroupData get(int id) {
        return utils.findOne(
            "SELECT * FROM MONSTER_GROUP WHERE MONSTER_GROUP_ID = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public boolean has(MonsterGroupData entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM MONSTER_GROUP WHERE MONSTER_GROUP_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public List<MonsterGroupData> all() {
        return utils.findAll("SELECT * FROM MONSTER_GROUP");
    }
}
