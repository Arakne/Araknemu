package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * SQL implementation for {@link PlayerItem} repository
 */
final class PlayerItemRepository implements fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository {
    private class Loader implements RepositoryUtils.Loader<PlayerItem> {
        @Override
        public PlayerItem create(ResultSet rs) throws SQLException {
            return new PlayerItem(
                rs.getInt("PLAYER_ID"),
                rs.getInt("ITEM_ENTRY_ID"),
                rs.getInt("ITEM_TEMPLATE_ID"),
                effectsTransformer.unserialize(rs.getString("ITEM_EFFECTS")),
                rs.getInt("QUANTITY"),
                rs.getInt("POSITION")
            );
        }

        @Override
        public PlayerItem fillKeys(PlayerItem entity, ResultSet keys) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<PlayerItem> utils;
    final private Transformer<List<ItemTemplateEffectEntry>> effectsTransformer;

    public PlayerItemRepository(ConnectionPool pool, Transformer<List<ItemTemplateEffectEntry>> effectsTransformer) {
        this.pool = new ConnectionPoolUtils(pool);
        this.effectsTransformer = effectsTransformer;
        this.utils = new RepositoryUtils<>(this.pool, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE PLAYER_ITEM (" +
                    "PLAYER_ID INTEGER," +
                    "ITEM_ENTRY_ID INTEGER," +
                    "ITEM_TEMPLATE_ID INTEGER," +
                    "ITEM_EFFECTS TEXT," +
                    "QUANTITY SMALLINT," +
                    "POSITION TINYINT," +
                    "PRIMARY KEY (PLAYER_ID, ITEM_ENTRY_ID)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE PLAYER_ITEM");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Collection<PlayerItem> byPlayer(Player player) {
        return utils.findAll(
            "SELECT * FROM PLAYER_ITEM WHERE PLAYER_ID = ?",
            stmt -> stmt.setInt(1, player.id())
        );
    }

    @Override
    public void update(PlayerItem item) {
        int count = utils.update(
            "UPDATE PLAYER_ITEM SET QUANTITY = ?, POSITION = ? WHERE PLAYER_ID = ? AND ITEM_ENTRY_ID = ?",
            stmt -> {
                stmt.setInt(1, item.quantity());
                stmt.setInt(2, item.position());
                stmt.setInt(3, item.playerId());
                stmt.setInt(4, item.entryId());
            }
        );

        if (count != 1) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void delete(PlayerItem item) {
        int count = utils.update(
            "DELETE FROM PLAYER_ITEM WHERE PLAYER_ID = ? AND ITEM_ENTRY_ID = ?",
            stmt -> {
                stmt.setInt(1, item.playerId());
                stmt.setInt(2, item.entryId());
            }
        );

        if (count != 1) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public PlayerItem add(PlayerItem entity) throws RepositoryException {
        utils.update(
            "INSERT INTO PLAYER_ITEM (PLAYER_ID, ITEM_ENTRY_ID, ITEM_TEMPLATE_ID, ITEM_EFFECTS, QUANTITY, POSITION) VALUES (?, ?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setInt(1,    entity.playerId());
                stmt.setInt(2,    entity.entryId());
                stmt.setInt(3,    entity.itemTemplateId());
                stmt.setString(4, effectsTransformer.serialize(entity.effects()));
                stmt.setInt(5,    entity.quantity());
                stmt.setInt(6,    entity.position());
            }
        );

        return entity;
    }

    @Override
    public PlayerItem get(PlayerItem entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM PLAYER_ITEM WHERE PLAYER_ID = ? AND ITEM_ENTRY_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.playerId());
                stmt.setInt(2, entity.entryId());
            }
        );
    }

    @Override
    public boolean has(PlayerItem entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM PLAYER_ITEM WHERE PLAYER_ID = ? AND ITEM_ENTRY_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.playerId());
                stmt.setInt(2, entity.entryId());
            }
        ) > 0;
    }
}
