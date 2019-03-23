package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardData;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardRepository;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * SQL implementation for monster reward repository
 */
final class SqlMonsterRewardRepository implements MonsterRewardRepository {
    private class Loader implements RepositoryUtils.Loader<MonsterRewardData> {
        @Override
        public MonsterRewardData create(ResultSet rs) throws SQLException {
            return new MonsterRewardData(
                rs.getInt("MONSTER_ID"),
                new Interval(
                    rs.getInt("MIN_KAMAS"),
                    rs.getInt("MAX_KAMAS")
                ),
                Arrays.stream(StringUtils.split(rs.getString("EXPERIENCES"), "|"))
                    .mapToLong(Long::parseLong)
                    .toArray()
            );
        }

        @Override
        public MonsterRewardData fillKeys(MonsterRewardData entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<MonsterRewardData> utils;

    public SqlMonsterRewardRepository(ConnectionPool pool) {
        this.pool = new ConnectionPoolUtils(pool);

        utils = new RepositoryUtils<>(this.pool, new SqlMonsterRewardRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE `MONSTER_REWARD` (" +
                    "`MONSTER_ID` INTEGER PRIMARY KEY," +
                    "`MIN_KAMAS` INTEGER," +
                    "`MAX_KAMAS` INTEGER," +
                    "`EXPERIENCES` VARCHAR(200)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE MONSTER_REWARD");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public MonsterRewardData get(MonsterRewardData entity) throws RepositoryException {
        return get(entity.id()).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Optional<MonsterRewardData> get(int id) {
        try {
            return Optional.of(utils.findOne(
                "SELECT * FROM MONSTER_REWARD WHERE MONSTER_ID = ?",
                stmt -> stmt.setInt(1, id)
            ));
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean has(MonsterRewardData entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM MONSTER_REWARD WHERE MONSTER_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public List<MonsterRewardData> all() {
        return utils.findAll("SELECT * FROM MONSTER_REWARD");
    }
}
