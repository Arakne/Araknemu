package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;
import fr.quatrevieux.araknemu.game.item.SuperType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * SQL implementation for {@link ItemType} repository
 */
final class SqlItemTypeRepository implements ItemTypeRepository {
    private class Loader implements RepositoryUtils.Loader<ItemType> {
        @Override
        public ItemType create(ResultSet rs) throws SQLException {
            return new ItemType(
                rs.getInt("TYPE_ID"),
                rs.getString("TYPE_NAME"),
                SuperType.values()[rs.getInt("SUPER_TYPE")],
                areaTransformer.unserialize(rs.getString("EFFECT_AREA"))
            );
        }

        @Override
        public ItemType fillKeys(ItemType entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<ItemType> utils;

    final private Transformer<EffectArea> areaTransformer;

    public SqlItemTypeRepository(ConnectionPool pool, Transformer<EffectArea> areaTransformer) {
        this.areaTransformer = areaTransformer;
        this.pool = new ConnectionPoolUtils(pool);
        utils = new RepositoryUtils<>(this.pool, new SqlItemTypeRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE `ITEM_TYPE` (" +
                    "`TYPE_ID` INTEGER PRIMARY KEY," +
                    "`TYPE_NAME` VARCHAR(32)," +
                    "`SUPER_TYPE` INTEGER," +
                    "`EFFECT_AREA` CHAR(2)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE ITEM_TYPE");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public ItemType get(ItemType entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public ItemType get(int id) {
        return utils.findOne(
            "SELECT * FROM ITEM_TYPE WHERE TYPE_ID = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public boolean has(ItemType entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM ITEM_TYPE WHERE TYPE_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<ItemType> load() {
        return utils.findAll("SELECT * FROM ITEM_TYPE");
    }
}
