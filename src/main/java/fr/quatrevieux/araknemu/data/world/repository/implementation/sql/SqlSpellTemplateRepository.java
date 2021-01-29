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
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 * SQL implementation for spell template repository
 */
final class SqlSpellTemplateRepository implements SpellTemplateRepository {
    private class Loader implements RepositoryUtils.Loader<SpellTemplate> {
        @Override
        public SpellTemplate create(ResultSet rs) throws SQLException {
            return new SpellTemplate(
                rs.getInt("SPELL_ID"),
                rs.getString("SPELL_NAME"),
                rs.getInt("SPELL_SPRITE"),
                rs.getString("SPELL_SPRITE_ARG"),
                new SpellTemplate.Level[]{
                    levelTransformer.unserialize(rs.getString("SPELL_LVL_1")),
                    levelTransformer.unserialize(rs.getString("SPELL_LVL_2")),
                    levelTransformer.unserialize(rs.getString("SPELL_LVL_3")),
                    levelTransformer.unserialize(rs.getString("SPELL_LVL_4")),
                    levelTransformer.unserialize(rs.getString("SPELL_LVL_5")),
                    levelTransformer.unserialize(rs.getString("SPELL_LVL_6")),
                },
                Arrays.stream(StringUtils.split(rs.getString("SPELL_TARGET"), ";"))
                    .mapToInt(Integer::parseInt)
                    .toArray()
            );
        }

        @Override
        public SpellTemplate fillKeys(SpellTemplate entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private QueryExecutor executor;
    final private RepositoryUtils<SpellTemplate> utils;

    final private Transformer<SpellTemplate.Level> levelTransformer;

    public SqlSpellTemplateRepository(QueryExecutor executor, Transformer<SpellTemplate.Level> levelTransformer) {
        this.executor = executor;
        this.levelTransformer = levelTransformer;
        utils = new RepositoryUtils<>(this.executor, new SqlSpellTemplateRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE `SPELL` (" +
                    "`SPELL_ID` INTEGER PRIMARY KEY," +
                    "`SPELL_NAME` VARCHAR(100)," +
                    "`SPELL_SPRITE` INTEGER," +
                    "`SPELL_SPRITE_ARG` VARCHAR(20)," +
                    "`SPELL_LVL_1` TEXT," +
                    "`SPELL_LVL_2` TEXT," +
                    "`SPELL_LVL_3` TEXT," +
                    "`SPELL_LVL_4` TEXT," +
                    "`SPELL_LVL_5` TEXT," +
                    "`SPELL_LVL_6` TEXT," +
                    "`SPELL_TARGET` VARCHAR(32)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE SPELL");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public SpellTemplate get(SpellTemplate entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public SpellTemplate get(int id) {
        try {
            return utils.findOne(
                "SELECT * FROM SPELL WHERE SPELL_ID = ?",
                stmt -> stmt.setInt(1, id)
            );
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Spell " + id + " is not found");
        }
    }

    @Override
    public boolean has(SpellTemplate entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM SPELL WHERE SPELL_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<SpellTemplate> load() {
        return utils.findAll("SELECT * FROM SPELL");
    }
}
