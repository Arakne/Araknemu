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

package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.BoostStatsData;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.SortedMap;

/**
 * SQL implementation of the repository
 */
final class SqlPlayerRaceRepository implements PlayerRaceRepository {
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
                new Position(
                    rs.getInt("ASTRUB_MAP_ID"),
                    rs.getInt("ASTRUB_CELL_ID")
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

    final private QueryExecutor executor;
    final private RepositoryUtils<PlayerRace> utils;
    final private Transformer<SortedMap<Integer, Characteristics>> characteristicsTransformer;
    final private Transformer<BoostStatsData> boostStatsDataTransformer;

    public SqlPlayerRaceRepository(QueryExecutor executor, Transformer<SortedMap<Integer, Characteristics>> characteristicsTransformer, Transformer<BoostStatsData> boostStatsDataTransformer) {
        this.characteristicsTransformer = characteristicsTransformer;
        this.boostStatsDataTransformer = boostStatsDataTransformer;

        this.executor = executor;
        utils = new RepositoryUtils<>(executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
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
                    "CELL_ID INTEGER," +
                    "ASTRUB_MAP_ID INTEGER," +
                    "ASTRUB_CELL_ID INTEGER" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE PLAYER_RACE");
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
        return utils.findAll("SELECT * FROM PLAYER_RACE");
    }
}
