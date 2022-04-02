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
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;
import fr.quatrevieux.araknemu.game.item.SuperType;
import org.checkerframework.checker.nullness.util.NullnessUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * SQL implementation for {@link ItemType} repository
 */
final class SqlItemTypeRepository implements ItemTypeRepository {
    private final QueryExecutor executor;
    private final RepositoryUtils<ItemType> utils;

    private final Transformer<EffectArea> areaTransformer;

    public SqlItemTypeRepository(QueryExecutor executor, Transformer<EffectArea> areaTransformer) {
        this.executor = executor;
        this.areaTransformer = areaTransformer;
        utils = new RepositoryUtils<>(this.executor, new SqlItemTypeRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE `ITEM_TYPE` (" +
                    "`TYPE_ID` INTEGER PRIMARY KEY," +
                    "`TYPE_NAME` VARCHAR(32)," +
                    "`SUPER_TYPE` INTEGER," +
                    "`EFFECT_AREA` CHAR(2)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE ITEM_TYPE");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public ItemType get(ItemType entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public ItemType get(int id) {
        return utils.findOne(
            "SELECT * FROM ITEM_TYPE WHERE TYPE_ID = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public boolean has(ItemType entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM ITEM_TYPE WHERE TYPE_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<ItemType> load() {
        return utils.findAll("SELECT * FROM ITEM_TYPE");
    }

    private class Loader implements RepositoryUtils.Loader<ItemType> {
        @Override
        public ItemType create(ResultSet rs) throws SQLException {
            return new ItemType(
                rs.getInt("TYPE_ID"),
                NullnessUtil.castNonNull(rs.getString("TYPE_NAME")),
                SuperType.byId(rs.getInt("SUPER_TYPE")),
                areaTransformer.unserialize(rs.getString("EFFECT_AREA"))
            );
        }

        @Override
        public ItemType fillKeys(ItemType entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }
}
