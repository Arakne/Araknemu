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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcExchange;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcExchangeRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Npc exchange repository implementation for SQL database
 */
final class SqlNpcExchangeRepository implements NpcExchangeRepository {
    private final QueryExecutor executor;
    private final RepositoryUtils<NpcExchange> utils;
    private final Transformer<Map<Integer, Integer>> itemsTransformer;

    public SqlNpcExchangeRepository(QueryExecutor executor, Transformer<Map<Integer, Integer>> itemsTransformer) {
        this.executor = executor;
        this.itemsTransformer = itemsTransformer;

        utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE NPC_EXCHANGE(" +
                    "NPC_EXCHANGE_ID INTEGER PRIMARY KEY," +
                    "NPC_TEMPLATE_ID INTEGER," +
                    "REQUIRED_KAMAS BIGINT," +
                    "REQUIRED_ITEMS TEXT," +
                    "EXCHANGED_KAMAS BIGINT," +
                    "EXCHANGED_ITEMS TEXT" +
                ")"
            );

            executor.query("CREATE INDEX IDX_NPC_TEMPLATE ON NPC_EXCHANGE (NPC_TEMPLATE_ID)");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE NPC_EXCHANGE");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public NpcExchange get(NpcExchange entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM NPC_EXCHANGE WHERE NPC_EXCHANGE_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        );
    }

    @Override
    public boolean has(NpcExchange entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM NPC_EXCHANGE WHERE NPC_EXCHANGE_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<NpcExchange> all() throws RepositoryException {
        return utils.findAll("SELECT * FROM NPC_EXCHANGE");
    }

    @Override
    public List<NpcExchange> byNpcTemplate(NpcTemplate template) {
        return utils.findAll("SELECT * FROM NPC_EXCHANGE WHERE NPC_TEMPLATE_ID = ?", stmt -> stmt.setInt(1, template.id()));
    }

    private class Loader implements RepositoryUtils.Loader<NpcExchange> {
        @Override
        public NpcExchange create(ResultSet rs) throws SQLException {
            return new NpcExchange(
                rs.getInt("NPC_EXCHANGE_ID"),
                rs.getInt("NPC_TEMPLATE_ID"),
                rs.getLong("REQUIRED_KAMAS"),
                itemsTransformer.unserialize(rs.getString("REQUIRED_ITEMS")),
                rs.getLong("EXCHANGED_KAMAS"),
                itemsTransformer.unserialize(rs.getString("EXCHANGED_ITEMS"))
            );
        }

        @Override
        public NpcExchange fillKeys(NpcExchange entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }
}
