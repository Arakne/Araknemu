package fr.quatrevieux.araknemu.data.world.repository.character;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Read only repository for {@link PlayerRace}
 */
final public class PlayerRaceRepository implements Repository<PlayerRace> {
    private static class Loader implements RepositoryUtils.Loader<PlayerRace> {
        final private Transformer<Characteristics> characteristicsTransformer;

        public Loader(Transformer<Characteristics> characteristicsTransformer) {
            this.characteristicsTransformer = characteristicsTransformer;
        }

        @Override
        public PlayerRace create(ResultSet rs) throws SQLException {
            return new PlayerRace(
                Race.byId(rs.getInt("RACE_ID")),
                rs.getString("RACE_NAME"),
                characteristicsTransformer.unserialize(rs.getString("RACE_STATS"))
            );
        }

        @Override
        public PlayerRace fillKeys(PlayerRace entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private Transformer<Characteristics> characteristicsTransformer;

    final private RepositoryUtils<PlayerRace> utils;

    public PlayerRaceRepository(ConnectionPool connection, Transformer<Characteristics> characteristicsTransformer) {
        this.characteristicsTransformer = characteristicsTransformer;

        pool = new ConnectionPoolUtils(connection);
        utils = new RepositoryUtils<>(pool, new Loader(
            characteristicsTransformer
        ));
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE PLAYER_RACE (" +
                    "RACE_ID INTEGER PRIMARY KEY," +
                    "RACE_NAME VARCHAR(32)," +
                    "RACE_STATS TEXT" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE PLAYER_RACE");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public PlayerRace get(PlayerRace entity) throws RepositoryException {
        return get(entity.race());
    }

    public PlayerRace get(Race race) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM PLAYER_RACE WHERE RACE_ID = ?",
            stmt -> stmt.setInt(1, race.ordinal())
        );
    }

    @Override
    public boolean has(PlayerRace entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM PLAYER_RACE WHERE RACE_ID = ?",
            stmt -> stmt.setInt(1, entity.race().ordinal())
        ) > 0;
    }
}
