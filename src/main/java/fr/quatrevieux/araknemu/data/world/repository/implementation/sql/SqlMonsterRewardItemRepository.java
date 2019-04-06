package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardItemRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQL implementation for monster reward item repository
 */
final class SqlMonsterRewardItemRepository implements MonsterRewardItemRepository {
    private class Loader implements RepositoryUtils.Loader<MonsterRewardItem> {
        @Override
        public MonsterRewardItem create(ResultSet rs) throws SQLException {
            return new MonsterRewardItem(
                rs.getInt("MONSTER_ID"),
                rs.getInt("ITEM_TEMPLATE_ID"),
                rs.getInt("QUANTITY"),
                rs.getInt("DISCERNMENT"),
                rs.getFloat("RATE")
            );
        }

        @Override
        public MonsterRewardItem fillKeys(MonsterRewardItem entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<MonsterRewardItem> utils;

    public SqlMonsterRewardItemRepository(ConnectionPool pool) {
        this.pool = new ConnectionPoolUtils(pool);

        utils = new RepositoryUtils<>(this.pool, new SqlMonsterRewardItemRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE `MONSTER_REWARD_ITEM` (" +
                    "`MONSTER_ID` INTEGER," +
                    "`ITEM_TEMPLATE_ID` INTEGER," +
                    "`QUANTITY` INTEGER," +
                    "`DISCERNMENT` INTEGER," +
                    "`RATE` INTEGER," +
                    "PRIMARY KEY (`MONSTER_ID`, `ITEM_TEMPLATE_ID`)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE MONSTER_REWARD_ITEM");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public MonsterRewardItem get(MonsterRewardItem entity) throws RepositoryException {
        throw new UnsupportedOperationException("No implemented");
    }

    @Override
    public boolean has(MonsterRewardItem entity) throws RepositoryException {
        throw new UnsupportedOperationException("No implemented");
    }

    @Override
    public List<MonsterRewardItem> byMonster(int monsterId) {
        return utils.findAll(
            "SELECT * FROM MONSTER_REWARD_ITEM WHERE MONSTER_ID = ?",
            stmt -> stmt.setInt(1, monsterId)
        );
    }

    @Override
    public Map<Integer, List<MonsterRewardItem>> all() {
        return utils.findAll("SELECT * FROM MONSTER_REWARD_ITEM")
            .stream()
            .collect(Collectors.groupingBy(MonsterRewardItem::monsterId))
        ;
    }
}
