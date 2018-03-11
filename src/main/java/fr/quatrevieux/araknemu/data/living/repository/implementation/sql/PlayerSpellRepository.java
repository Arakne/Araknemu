package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * SQL implementation for {@link PlayerSpell} repository
 */
final class PlayerSpellRepository implements fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository {
    private class Loader implements RepositoryUtils.Loader<PlayerSpell> {
        @Override
        public PlayerSpell create(ResultSet rs) throws SQLException {
            return new PlayerSpell(
                rs.getInt("PLAYER_ID"),
                rs.getInt("SPELL_ID"),
                rs.getBoolean("CLASS_SPELL"),
                rs.getInt("SPELL_LEVEL"),
                rs.getString("SPELL_POSITION").charAt(0)
            );
        }

        @Override
        public PlayerSpell fillKeys(PlayerSpell entity, ResultSet keys) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<PlayerSpell> utils;

    public PlayerSpellRepository(ConnectionPool pool) {
        this.pool = new ConnectionPoolUtils(pool);
        this.utils = new RepositoryUtils<>(this.pool, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE PLAYER_SPELL (" +
                    "PLAYER_ID INTEGER," +
                    "SPELL_ID INTEGER," +
                    "CLASS_SPELL BOOLEAN," +
                    "SPELL_LEVEL TINYINT," +
                    "SPELL_POSITION CHAR(1)," +
                    "PRIMARY KEY (PLAYER_ID, SPELL_ID)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE PLAYER_SPELL");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Collection<PlayerSpell> byPlayer(Player player) {
        return utils.findAll(
            "SELECT * FROM PLAYER_SPELL WHERE PLAYER_ID = ?",
            stmt -> stmt.setInt(1, player.id())
        );
    }

    @Override
    public void delete(PlayerSpell item) {
        int count = utils.update(
            "DELETE FROM PLAYER_SPELL WHERE PLAYER_ID = ? AND SPELL_ID = ?",
            stmt -> {
                stmt.setInt(1, item.playerId());
                stmt.setInt(2, item.spellId());
            }
        );

        if (count != 1) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public PlayerSpell add(PlayerSpell entity) throws RepositoryException {
        utils.update(
            "REPLACE INTO PLAYER_SPELL (PLAYER_ID, SPELL_ID, CLASS_SPELL, SPELL_LEVEL, SPELL_POSITION) VALUES (?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setInt(1,     entity.playerId());
                stmt.setInt(2,     entity.spellId());
                stmt.setBoolean(3, entity.classSpell());
                stmt.setInt(4,     entity.level());
                stmt.setString(5,  entity.position() + "");
            }
        );

        return entity;
    }

    @Override
    public PlayerSpell get(PlayerSpell entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM PLAYER_SPELL WHERE PLAYER_ID = ? AND SPELL_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.playerId());
                stmt.setInt(2, entity.spellId());
            }
        );
    }

    @Override
    public boolean has(PlayerSpell entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM PLAYER_SPELL WHERE PLAYER_ID = ? AND SPELL_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.playerId());
                stmt.setInt(2, entity.spellId());
            }
        ) > 0;
    }
}
