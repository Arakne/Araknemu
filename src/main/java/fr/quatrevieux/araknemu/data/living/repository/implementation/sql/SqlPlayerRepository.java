/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.value.ServerCharacters;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

final class SqlPlayerRepository implements PlayerRepository {
    private class Loader implements RepositoryUtils.Loader<Player> {
        @Override
        public Player create(ResultSet rs) throws SQLException {
            return new Player(
                rs.getInt("PLAYER_ID"),
                rs.getInt("ACCOUNT_ID"),
                rs.getInt("SERVER_ID"),
                rs.getString("PLAYER_NAME"),
                Race.byId(rs.getInt("RACE")),
                Gender.values()[rs.getInt("SEX")],
                new Colors(
                    rs.getInt("COLOR1"),
                    rs.getInt("COLOR2"),
                    rs.getInt("COLOR3")
                ),
                rs.getInt("PLAYER_LEVEL"),
                characteristicsTransformer.unserialize(
                    rs.getString("PLAYER_STATS")
                ),
                new Position(
                    rs.getInt("MAP_ID"),
                    rs.getInt("CELL_ID")
                ),
                channelsTransformer.unserialize(rs.getString("CHANNELS")),
                rs.getInt("BOOST_POINTS"),
                rs.getInt("SPELL_POINTS"),
                rs.getInt("LIFE_POINTS"),
                rs.getLong("PLAYER_EXPERIENCE"),
                new Position(
                    rs.getInt("SAVED_MAP_ID"),
                    rs.getInt("SAVED_CELL_ID")
                ),
                rs.getLong("PLAYER_KAMAS")
            );
        }

        @Override
        public Player fillKeys(Player entity, ResultSet keys) throws SQLException {
            return entity.withId(
                keys.getInt(1)
            );
        }
    }

    final private QueryExecutor executor;
    final private Transformer<MutableCharacteristics> characteristicsTransformer;
    final private Transformer<Set<ChannelType>> channelsTransformer;

    final private RepositoryUtils<Player> utils;

    public SqlPlayerRepository(QueryExecutor executor, Transformer<MutableCharacteristics> characteristicsTransformer, Transformer<Set<ChannelType>> channelsTransformer) {
        this.executor = executor;
        this.characteristicsTransformer = characteristicsTransformer;
        this.channelsTransformer = channelsTransformer;
        this.utils = new RepositoryUtils<>(this.executor, new SqlPlayerRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
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
                    "PLAYER_STATS TEXT," +
                    "MAP_ID INTEGER," +
                    "CELL_ID INTEGER," +
                    "CHANNELS VARCHAR(16)," +
                    "BOOST_POINTS INTEGER," +
                    "SPELL_POINTS INTEGER," +
                    "LIFE_POINTS INTEGER," +
                    "PLAYER_EXPERIENCE BIGINT," +
                    "SAVED_MAP_ID INTEGER," +
                    "SAVED_CELL_ID INTEGER," +
                    "PLAYER_KAMAS BIGINT," +
                    "UNIQUE (PLAYER_NAME, SERVER_ID)" +
                ")"
            );

            executor.query("CREATE INDEX IDX_ACC_SRV ON PLAYER (ACCOUNT_ID, SERVER_ID)");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE PLAYER");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Player add(Player entity) throws RepositoryException {
        return utils.update(
            "INSERT INTO PLAYER " +
                "(ACCOUNT_ID, SERVER_ID, PLAYER_NAME, RACE, SEX, COLOR1, COLOR2, COLOR3, PLAYER_LEVEL, PLAYER_STATS, MAP_ID, CELL_ID, CHANNELS, BOOST_POINTS, SPELL_POINTS, LIFE_POINTS, PLAYER_EXPERIENCE, SAVED_MAP_ID, SAVED_CELL_ID, PLAYER_KAMAS) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setInt(1,     entity.accountId());
                stmt.setInt(2,     entity.serverId());
                stmt.setString(3,  entity.name());
                stmt.setInt(4,     entity.race().ordinal());
                stmt.setInt(5,     entity.gender().ordinal());
                stmt.setInt(6,     entity.colors().color1());
                stmt.setInt(7,     entity.colors().color2());
                stmt.setInt(8,     entity.colors().color3());
                stmt.setInt(9,     entity.level());
                stmt.setString(10, characteristicsTransformer.serialize(entity.stats()));
                stmt.setInt(11,    entity.position().map());
                stmt.setInt(12,    entity.position().cell());
                stmt.setString(13, channelsTransformer.serialize(entity.channels()));
                stmt.setInt(14,    entity.boostPoints());
                stmt.setInt(15,    entity.spellPoints());
                stmt.setInt(16,    entity.life());
                stmt.setLong(17,   entity.experience());
                stmt.setInt(18,    entity.savedPosition().map());
                stmt.setInt(19,    entity.savedPosition().cell());
                stmt.setLong(20,   entity.kamas());
            },
            entity
        );
    }

    @Override
    public void delete(Player entity) throws RepositoryException {
        if (utils.update("DELETE FROM PLAYER WHERE PLAYER_ID = ?", rs -> rs.setInt(1, entity.id())) < 1) {
            throw new EntityNotFoundException();
        }
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

    @Override
    public Collection<Player> findByAccount(int accountId, int serverId) {
        return utils.findAll(
            "SELECT * FROM PLAYER WHERE ACCOUNT_ID = ? AND SERVER_ID = ?",
            stmt -> {
                stmt.setInt(1, accountId);
                stmt.setInt(2, serverId);
            }
        );
    }

    @Override
    public boolean nameExists(Player player) {
        return utils.aggregate(
            "SELECT COUNT(*) FROM PLAYER WHERE PLAYER_NAME = ? AND SERVER_ID = ?",
            stmt -> {
                stmt.setString(1, player.name());
                stmt.setInt(2,    player.serverId());
            }
        ) > 0;
    }

    @Override
    public int accountCharactersCount(Player player) {
        return utils.aggregate(
            "SELECT COUNT(*) FROM PLAYER WHERE ACCOUNT_ID = ? AND SERVER_ID = ?",
            stmt -> {
                stmt.setInt(1, player.accountId());
                stmt.setInt(2, player.serverId());
            }
        );
    }

    @Override
    public Collection<ServerCharacters> accountCharactersCount(int accountId) {
        try {
            return executor.prepare(
                "SELECT SERVER_ID, COUNT(*) FROM PLAYER WHERE ACCOUNT_ID = ? GROUP BY SERVER_ID",
                stmt -> {
                    stmt.setInt(1, accountId);

                    try (ResultSet rs = stmt.executeQuery()) {
                        Collection<ServerCharacters> list = new ArrayList<>();

                        while (rs.next()) {
                            list.add(
                                new ServerCharacters(
                                    rs.getInt("SERVER_ID"),
                                    rs.getInt("COUNT(*)")
                                )
                            );
                        }

                        return list;
                    }
                }
            );
        } catch (SQLException e) {
            throw new RepositoryException("Cannot load characters count", e);
        }
    }

    @Override
    public Collection<ServerCharacters> serverCharactersCountByAccountPseudo(String accountPseudo) {
        try {
            return executor.prepare(
                "SELECT SERVER_ID, COUNT(*) FROM PLAYER P JOIN ACCOUNT A ON P.ACCOUNT_ID = A.ACCOUNT_ID WHERE A.PSEUDO = ? GROUP BY SERVER_ID",
                stmt -> {
                    stmt.setString(1, accountPseudo);

                    try (ResultSet rs = stmt.executeQuery()) {
                        Collection<ServerCharacters> list = new ArrayList<>();

                        while (rs.next()) {
                            list.add(new ServerCharacters(rs.getInt("SERVER_ID"), rs.getInt("COUNT(*)")));
                        }

                        return list;
                    }
                }
            );
        } catch (SQLException e) {
            throw new RepositoryException("Cannot load characters count", e);
        }
    }

    @Override
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

    @Override
    public void save(Player player) {
        int rows = utils.update(
            "UPDATE PLAYER SET " +
                "PLAYER_LEVEL = ?, PLAYER_STATS = ?, MAP_ID = ?, CELL_ID = ?, CHANNELS = ?, BOOST_POINTS = ?, SPELL_POINTS = ?, LIFE_POINTS = ?, PLAYER_EXPERIENCE = ?, SAVED_MAP_ID = ?, SAVED_CELL_ID = ?, PLAYER_KAMAS = ? " +
                "WHERE PLAYER_ID = ?",
            stmt -> {
                stmt.setInt(1,    player.level());
                stmt.setString(2, characteristicsTransformer.serialize(player.stats()));
                stmt.setInt(3,    player.position().map());
                stmt.setInt(4,    player.position().cell());
                stmt.setString(5, channelsTransformer.serialize(player.channels()));
                stmt.setInt(6,    player.boostPoints());
                stmt.setInt(7,    player.spellPoints());
                stmt.setInt(8,    player.life());
                stmt.setLong(9,   player.experience());
                stmt.setInt(10,   player.savedPosition().map());
                stmt.setInt(11,   player.savedPosition().cell());
                stmt.setLong(12,  player.kamas());
                stmt.setInt(13,   player.id());
            }
        );

        if (rows != 1) {
            throw new EntityNotFoundException();
        }
    }
}
