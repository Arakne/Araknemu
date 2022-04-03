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
import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.Record;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterTemplate;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterTemplateRepository;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import fr.quatrevieux.araknemu.util.ParseUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.index.qual.SameLen;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL implementation for monster template repository
 */
final class SqlMonsterTemplateRepository implements MonsterTemplateRepository {
    private final QueryExecutor executor;
    private final RepositoryUtils<MonsterTemplate> utils;
    private final Transformer<Colors> colorsTransformer;
    private final Transformer<Characteristics> characteristicsTransformer;

    public SqlMonsterTemplateRepository(QueryExecutor executor, Transformer<Colors> colorsTransformer, Transformer<Characteristics> characteristicsTransformer) {
        this.executor = executor;

        this.colorsTransformer = colorsTransformer;
        this.characteristicsTransformer = characteristicsTransformer;

        utils = new RepositoryUtils<>(this.executor, new SqlMonsterTemplateRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE `MONSTER_TEMPLATE` (" +
                    "  `MONSTER_ID` INTEGER PRIMARY KEY," +
                    "  `MONSTER_NAME` VARCHAR(100)," +
                    "  `GFXID` INTEGER," +
                    "  `COLORS` VARCHAR(30)," +
                    "  `AI` VARCHAR(12)," +
                    "  `CHARACTERISTICS` TEXT," +
                    "  `LIFE_POINTS` VARCHAR(200)," +
                    "  `INITIATIVES` VARCHAR(200)," +
                    "  `SPELLS` TEXT" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE MONSTER_TEMPLATE");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public MonsterTemplate get(MonsterTemplate entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public MonsterTemplate get(int id) {
        try {
            return utils.findOne(
                "SELECT * FROM MONSTER_TEMPLATE WHERE MONSTER_ID = ?",
                stmt -> stmt.setInt(1, id)
            );
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Monster " + id + " is not found");
        }
    }

    @Override
    public boolean has(MonsterTemplate entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM MONSTER_TEMPLATE WHERE MONSTER_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public List<MonsterTemplate> all() {
        return utils.findAll("SELECT * FROM MONSTER_TEMPLATE");
    }

    private class Loader implements RepositoryUtils.Loader<MonsterTemplate> {
        @Override
        public MonsterTemplate create(Record record) throws SQLException {
            final String[] characteristics = record.getCsvArray("CHARACTERISTICS", '|');
            final String[] lifePoints = record.getCsvArray("LIFE_POINTS", '|');
            final String[] initiatives = record.getCsvArray("INITIATIVES", '|');
            final String[] spells = record.getCsvArray("SPELLS", '|');

            if (characteristics.length != lifePoints.length || characteristics.length != initiatives.length || characteristics.length != spells.length) {
                throw new IllegalArgumentException("All grade characteristics must have the same length");
            }

            return new MonsterTemplate(
                record.getInt("MONSTER_ID"),
                record.getString("MONSTER_NAME"),
                record.getInt("GFXID"),
                record.unserialize("COLORS", colorsTransformer),
                record.getString("AI"),
                parseGrades(characteristics, lifePoints, initiatives, spells)
            );
        }

        @Override
        public MonsterTemplate fillKeys(MonsterTemplate entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }

        private MonsterTemplate.Grade[] parseGrades(String @SameLen({"#2", "#3", "#4"}) [] characteristics, String @SameLen({"#1", "#3", "#4"}) [] lifePoints, String @SameLen({"#1", "#2", "#4"}) [] initiatives, String @SameLen({"#1", "#2", "#3"}) [] spells) {
            final MonsterTemplate.Grade[] grades = new MonsterTemplate.Grade[characteristics.length];

            for (int i = 0; i < characteristics.length; ++i) {
                final String[] grade = StringUtils.splitPreserveAllTokens(characteristics[i], "@", 2);

                if (grade.length != 2) {
                    throw new TransformerException("Invalid grade '" + grades[i] + "'");
                }

                final Map<Integer, @Positive Integer> gradeSpells = new HashMap<>();

                for (String spell : StringUtils.split(spells[i], ";")) {
                    final String[] data = StringUtils.split(spell, "@", 2);

                    if (data.length != 2) {
                        throw new TransformerException("Invalid spell list '" + spells[i] + "'");
                    }

                    gradeSpells.put(
                        Integer.parseInt(data[0]),
                        ParseUtils.parsePositiveInt(data[1])
                    );
                }

                grades[i] = new MonsterTemplate.Grade(
                    ParseUtils.parsePositiveInt(grade[0]),
                    ParseUtils.parsePositiveInt(lifePoints[i]),
                    Integer.parseInt(initiatives[i]),
                    characteristicsTransformer.unserialize(grade[1]),
                    gradeSpells
                );
            }

            return grades;
        }
    }
}
