package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerExperience;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * SQL implementation of the repository
 */
final class PlayerExperienceRepository implements fr.quatrevieux.araknemu.data.world.repository.character.PlayerExperienceRepository {
    private class Loader implements RepositoryUtils.Loader<PlayerExperience> {
        @Override
        public PlayerExperience create(ResultSet rs) throws SQLException {
            return new PlayerExperience(
                rs.getInt("PLAYER_LEVEL"),
                rs.getLong("EXPERIENCE")
            );
        }

        @Override
        public PlayerExperience fillKeys(PlayerExperience entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<PlayerExperience> utils;

    public PlayerExperienceRepository(ConnectionPool connection) {
        pool  = new ConnectionPoolUtils(connection);
        utils = new RepositoryUtils<>(pool, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE PLAYER_XP (" +
                    "PLAYER_LEVEL SMALLINT PRIMARY KEY," +
                    "EXPERIENCE BIGINT" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE PLAYER_XP");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public PlayerExperience get(PlayerExperience entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM PLAYER_XP WHERE PLAYER_LEVEL = ?",
            stmt -> stmt.setInt(1, entity.level())
        );
    }

    @Override
    public boolean has(PlayerExperience entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM PLAYER_XP WHERE PLAYER_LEVEL = ?",
            stmt -> stmt.setInt(1, entity.level())
        ) > 0;
    }

    @Override
    public List<PlayerExperience> all() {
        return utils.findAll("SELECT * FROM PLAYER_XP ORDER BY PLAYER_LEVEL ASC");
    }
}
