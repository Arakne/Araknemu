package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.BoostStatsData;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 * SQL implementation of the repository
 */
final class PlayerRaceRepository implements fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository {
    private class Loader implements RepositoryUtils.Loader<PlayerRace> {
        @Override
        public PlayerRace create(ResultSet rs) throws SQLException {
            return new PlayerRace(
                Race.byId(rs.getInt("RACE_ID")),
                rs.getString("RACE_NAME"),
                characteristicsTransformer.unserialize(rs.getString("RACE_STATS")),
                rs.getInt("START_DISCERNMENT"),
                rs.getInt("START_PODS"),
                rs.getInt("START_LIFE"),
                rs.getInt("PER_LEVEL_LIFE"),
                boostStatsDataTransformer.unserialize(rs.getString("STATS_BOOST")),
                new Position(
                    rs.getInt("MAP_ID"),
                    rs.getInt("CELL_ID")
                ),
                Arrays.stream(StringUtils.split(rs.getString("RACE_SPELLS"), "|"))
                    .mapToInt(Integer::parseInt)
                    .toArray()
            );
        }

        @Override
        public PlayerRace fillKeys(PlayerRace entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<PlayerRace> utils;
    final private Transformer<Characteristics> characteristicsTransformer;
    final private Transformer<BoostStatsData> boostStatsDataTransformer;

    public PlayerRaceRepository(ConnectionPool connection, Transformer<Characteristics> characteristicsTransformer, Transformer<BoostStatsData> boostStatsDataTransformer) {
        this.characteristicsTransformer = characteristicsTransformer;
        this.boostStatsDataTransformer = boostStatsDataTransformer;

        pool = new ConnectionPoolUtils(connection);
        utils = new RepositoryUtils<>(pool, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE PLAYER_RACE (" +
                    "RACE_ID INTEGER PRIMARY KEY," +
                    "RACE_NAME VARCHAR(32)," +
                    "RACE_STATS TEXT," +
                    "START_DISCERNMENT INTEGER," +
                    "START_PODS INTEGER," +
                    "START_LIFE INTEGER," +
                    "PER_LEVEL_LIFE INTEGER," +
                    "STATS_BOOST VARCHAR(255)," +
                    "RACE_SPELLS VARCHAR(255)," +
                    "MAP_ID INTEGER," +
                    "CELL_ID INTEGER" +
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

    @Override
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

    @Override
    public Collection<PlayerRace> load() {
        return utils.findAll(
            "SELECT * FROM PLAYER_RACE",
            s -> {}
        );
    }
}
