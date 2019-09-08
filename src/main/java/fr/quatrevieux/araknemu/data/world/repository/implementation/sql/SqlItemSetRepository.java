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

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * SQL implementation for {@link ItemSet} repository
 */
final class SqlItemSetRepository implements ItemSetRepository {
    private class Loader implements RepositoryUtils.Loader<ItemSet> {
        @Override
        public ItemSet create(ResultSet rs) throws SQLException {
            return new ItemSet(
                rs.getInt("ITEM_SET_ID"),
                rs.getString("ITEM_SET_NAME"),
                bonusTransformer.unserialize(rs.getString("ITEM_SET_BONUS"))
            );
        }

        @Override
        public ItemSet fillKeys(ItemSet entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private QueryExecutor executor;
    final private RepositoryUtils<ItemSet> utils;

    final private Transformer<List<List<ItemTemplateEffectEntry>>> bonusTransformer;

    public SqlItemSetRepository(QueryExecutor executor, Transformer<List<List<ItemTemplateEffectEntry>>> bonusTransformer) {
        this.executor = executor;
        this.bonusTransformer = bonusTransformer;
        utils = new RepositoryUtils<>(this.executor, new SqlItemSetRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE `ITEM_SET` (" +
                    "`ITEM_SET_ID` INTEGER PRIMARY KEY," +
                    "`ITEM_SET_NAME` VARCHAR(50)," +
                    "`ITEM_SET_BONUS` TEXT" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE ITEM_SET");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public ItemSet get(ItemSet entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public ItemSet get(int id) {
        return utils.findOne(
            "SELECT * FROM ITEM_SET WHERE ITEM_SET_ID = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public boolean has(ItemSet entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM ITEM_SET WHERE ITEM_SET_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<ItemSet> load() {
        return utils.findAll("SELECT * FROM ITEM_SET");
    }
}
