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

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Npc repository implementation for SQL database
 *
 * @see Npc
 */
final class SqlNpcRepository implements NpcRepository {
    private class Loader implements RepositoryUtils.Loader<Npc> {
        @Override
        public Npc create(ResultSet rs) throws SQLException {
            return new Npc(
                rs.getInt("NPC_ID"),
                rs.getInt("NPC_TEMPLATE_ID"),
                new Position(
                    rs.getInt("MAP_ID"),
                    rs.getInt("CELL_ID")
                ),
                Direction.values()[rs.getInt("ORIENTATION")],
                Arrays.stream(StringUtils.split(rs.getString("QUESTIONS"), ';'))
                    .mapToInt(Integer::parseInt)
                    .toArray()
            );
        }

        @Override
        public Npc fillKeys(Npc entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private QueryExecutor executor;
    final private RepositoryUtils<Npc> utils;

    public SqlNpcRepository(QueryExecutor executor) {
        this.executor = executor;

        utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE NPC(" +
                    "NPC_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NPC_TEMPLATE_ID INTEGER," +
                    "MAP_ID INTEGER," +
                    "CELL_ID INTEGER," +
                    "ORIENTATION TINYINT(1)," +
                    "QUESTIONS VARCHAR(32)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE NPC");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Npc get(int id) {
        return utils.findOne(
            "SELECT * FROM NPC WHERE NPC_ID = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public Npc get(Npc entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(Npc entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM NPC WHERE NPC_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<Npc> all() throws RepositoryException {
        return utils.findAll("SELECT * FROM NPC");
    }

    @Override
    public Collection<Npc> byMapId(int mapId) {
        return utils.findAll(
            "SELECT * FROM NPC WHERE MAP_ID = ?",
            rs -> rs.setInt(1, mapId)
        );
    }
}
