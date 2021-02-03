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
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * SQL implementation for item template repository
 */
final class SqlItemTemplateRepository implements ItemTemplateRepository {
    private final QueryExecutor executor;
    private final RepositoryUtils<ItemTemplate> utils;

    private final Transformer<List<ItemTemplateEffectEntry>> effectsTransformer;

    public SqlItemTemplateRepository(QueryExecutor executor, Transformer<List<ItemTemplateEffectEntry>> effectsTransformer) {
        this.executor = executor;
        this.effectsTransformer = effectsTransformer;
        utils = new RepositoryUtils<>(this.executor, new SqlItemTemplateRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE `ITEM_TEMPLATE` (" +
                    "  `ITEM_TEMPLATE_ID` INTEGER PRIMARY KEY," +
                    "  `ITEM_TYPE` INTEGER," +
                    "  `ITEM_NAME` VARCHAR(50)," +
                    "  `ITEM_LEVEL` INTEGER," +
                    "  `ITEM_EFFECTS` TEXT," +
                    "  `WEIGHT` INTEGER," +
                    "  `ITEM_SET_ID` INTEGER," +
                    "  `PRICE` INTEGER," +
                    "  `CONDITIONS` VARCHAR(100)," +
                    "  `WEAPON_INFO` VARCHAR(100)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE ITEM_TEMPLATE");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public ItemTemplate get(ItemTemplate entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public ItemTemplate get(int id) {
        return utils.findOne(
            "SELECT * FROM ITEM_TEMPLATE WHERE ITEM_TEMPLATE_ID = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public boolean has(ItemTemplate entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM ITEM_TEMPLATE WHERE ITEM_TEMPLATE_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<ItemTemplate> load() {
        return utils.findAll("SELECT * FROM ITEM_TEMPLATE");
    }

    private class Loader implements RepositoryUtils.Loader<ItemTemplate> {
        @Override
        public ItemTemplate create(ResultSet rs) throws SQLException {
            return new ItemTemplate(
                rs.getInt("ITEM_TEMPLATE_ID"),
                rs.getInt("ITEM_TYPE"),
                rs.getString("ITEM_NAME"),
                rs.getInt("ITEM_LEVEL"),
                effectsTransformer.unserialize(rs.getString("ITEM_EFFECTS")),
                rs.getInt("WEIGHT"),
                rs.getString("CONDITIONS"),
                rs.getInt("ITEM_SET_ID"),
                rs.getString("WEAPON_INFO"),
                rs.getInt("PRICE")
            );
        }

        @Override
        public ItemTemplate fillKeys(ItemTemplate entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }
}
