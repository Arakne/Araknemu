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

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 * Map repository implementation for SQL database
 */
final class SqlNpcTemplateRepository implements NpcTemplateRepository {
    private class Loader implements RepositoryUtils.Loader<NpcTemplate> {
        @Override
        public NpcTemplate create(ResultSet rs) throws SQLException {
            final String store = rs.getString("STORE_ITEMS");

            return new NpcTemplate(
                rs.getInt("NPC_TEMPLATE_ID"),
                rs.getInt("GFXID"),
                rs.getInt("SCALE_X"),
                rs.getInt("SCALE_Y"),
                Gender.values()[rs.getInt("SEX")],
                new Colors(
                    rs.getInt("COLOR1"),
                    rs.getInt("COLOR2"),
                    rs.getInt("COLOR3")
                ),
                rs.getString("ACCESSORIES"),
                rs.getInt("EXTRA_CLIP"),
                rs.getInt("CUSTOM_ARTWORK"),
                store == null
                    ? null
                    : Arrays.stream(StringUtils.split(store, ","))
                        .mapToInt(Integer::parseInt)
                        .toArray()
            );
        }

        @Override
        public NpcTemplate fillKeys(NpcTemplate entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private QueryExecutor executor;
    final private RepositoryUtils<NpcTemplate> utils;

    public SqlNpcTemplateRepository(QueryExecutor executor) {
        this.executor = executor;

        utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE NPC_TEMPLATE(" +
                    "NPC_TEMPLATE_ID INTEGER PRIMARY KEY," +
                    "GFXID INTEGER," +
                    "SCALE_X INTEGER," +
                    "SCALE_Y INTEGER," +
                    "SEX TINYINT(1)," +
                    "COLOR1 INTEGER," +
                    "COLOR2 INTEGER," +
                    "COLOR3 INTEGER," +
                    "ACCESSORIES VARCHAR(30)," +
                    "EXTRA_CLIP TINYINT(1)," +
                    "CUSTOM_ARTWORK INTEGER," +
                    "STORE_ITEMS TEXT" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE NPC_TEMPLATE");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public NpcTemplate get(int id) {
        return utils.findOne(
            "SELECT * FROM NPC_TEMPLATE WHERE NPC_TEMPLATE_ID = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public NpcTemplate get(NpcTemplate entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(NpcTemplate entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM NPC_TEMPLATE WHERE NPC_TEMPLATE_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<NpcTemplate> all() throws RepositoryException {
        return utils.findAll("SELECT * FROM NPC_TEMPLATE");
    }
}
