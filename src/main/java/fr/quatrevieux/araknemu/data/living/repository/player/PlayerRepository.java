package fr.quatrevieux.araknemu.data.living.repository.player;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Repository for {@link Player} entity
 */
final public class PlayerRepository implements MutableRepository<Player> {
    private static class Loader implements RepositoryUtils.Loader<Player> {
        @Override
        public Player create(ResultSet rs) throws SQLException {
            return new Player(
                rs.getInt("PLAYER_ID"),
                rs.getInt("ACCOUNT_ID"),
                rs.getInt("SERVER_ID"),
                rs.getString("PLAYER_NAME"),
                Race.byId(rs.getInt("RACE")),
                Sex.values()[rs.getInt("SEX")],
                new Colors(
                    rs.getInt("COLOR1"),
                    rs.getInt("COLOR2"),
                    rs.getInt("COLOR3")
                ),
                rs.getInt("PLAYER_LEVEL")
            );
        }

        @Override
        public Player fillKeys(Player entity, ResultSet keys) throws SQLException {
            return entity.withId(
                keys.getInt(1)
            );
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<Player> utils;

    public PlayerRepository(ConnectionPool pool) {
        this.pool = new ConnectionPoolUtils(pool);
        this.utils = new RepositoryUtils<>(this.pool, new PlayerRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE PLAYER (" +
                    "PLAYER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ACCOUNT_ID INTEGER," +
                    "SERVER_ID INTEGER," +
                    "PLAYER_NAME VARCHAR(32)," +
                    "RACE INTEGER(2)," +
                    "SEX INTEGER(1)," +
                    "COLOR1 INTEGER," +
                    "COLOR2 INTEGER," +
                    "COLOR3 INTEGER," +
                    "PLAYER_LEVEL INTEGER," +
                    "UNIQUE (PLAYER_NAME, SERVER_ID)" +
                ")"
            );

            pool.query("CREATE INDEX IDX_ACC_SRV ON PLAYER (ACCOUNT_ID, SERVER_ID)");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE PLAYER");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Player add(Player entity) throws RepositoryException {
        return utils.update(
            "INSERT INTO PLAYER " +
                "(ACCOUNT_ID, SERVER_ID, PLAYER_NAME, RACE, SEX, COLOR1, COLOR2, COLOR3, PLAYER_LEVEL) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setInt(1,    entity.accountId());
                stmt.setInt(2,    entity.serverId());
                stmt.setString(3, entity.name());
                stmt.setInt(4,    entity.race().ordinal());
                stmt.setInt(5,    entity.sex().ordinal());
                stmt.setInt(6,    entity.colors().color1());
                stmt.setInt(7,    entity.colors().color2());
                stmt.setInt(8,    entity.colors().color3());
                stmt.setInt(9,    entity.level());
            },
            entity
        );
    }

    @Override
    public void delete(Player entity) throws RepositoryException {
        utils.update("DELETE FROM PLAYER WHERE PLAYER_ID = ?", rs -> rs.setInt(1, entity.id()));
    }

    @Override
    public Player get(Player entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM PLAYER WHERE PLAYER_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        );
    }

    @Override
    public boolean has(Player entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM PLAYER WHERE PLAYER_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    /**
     * Get list of players by account
     *
     * @param accountId The account id
     * @param serverId The server
     */
    public Collection<Player> findByAccount(int accountId, int serverId) {
        return utils.findAll(
            "SELECT * FROM PLAYER WHERE ACCOUNT_ID = ? AND SERVER_ID = ?",
            stmt -> {
                stmt.setInt(1, accountId);
                stmt.setInt(2, serverId);
            }
        );
    }

    public boolean nameExists(Player player) {
        return utils.aggregate(
            "SELECT COUNT(*) FROM PLAYER WHERE PLAYER_NAME = ? AND SERVER_ID = ?",
            stmt -> {
                stmt.setString(1, player.name());
                stmt.setInt(2,    player.serverId());
            }
        ) > 0;
    }

    /**
     * Get the account characters count
     *
     * @param player The player data
     */
    public int accountCharactersCount(Player player) {
        return utils.aggregate(
            "SELECT COUNT(*) FROM PLAYER WHERE ACCOUNT_ID = ? AND SERVER_ID = ?",
            stmt -> {
                stmt.setInt(1, player.accountId());
                stmt.setInt(2, player.serverId());
            }
        );
    }

    /**
     * Get the player entity by id, and ensure that account and server is valid
     */
    public Player getForGame(Player player) {
        return utils.findOne(
            "SELECT * FROM PLAYER WHERE PLAYER_ID = ? AND ACCOUNT_ID = ? AND SERVER_ID = ?",
            stmt -> {
                stmt.setInt(1, player.id());
                stmt.setInt(2, player.accountId());
                stmt.setInt(3, player.serverId());
            }
        );
    }
}
