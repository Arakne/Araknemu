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

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.transformer.ImmutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterTemplate;
import fr.quatrevieux.araknemu.data.world.transformer.ColorsTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SqlMonsterTemplateRepositoryTest extends GameBaseCase {
    private SqlMonsterTemplateRepository repository;
    private ConnectionPoolExecutor executor;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplates();

        repository = new SqlMonsterTemplateRepository(
            executor = new ConnectionPoolExecutor(app.database().get("game")),
            container.get(ColorsTransformer.class),
            container.get(ImmutableCharacteristicsTransformer.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        MonsterTemplate template = repository.get(31);

        assertEquals(31, template.id());
        assertEquals("Larve Bleue", template.name());
        assertEquals(1563, template.gfxId());
        assertEquals("AGGRESSIVE", template.ai());
        assertCount(5, template.grades());
        assertEquals(-1, template.colors().color1());
        assertEquals(-1, template.colors().color2());
        assertEquals(-1, template.colors().color3());

        assertEquals(2, template.grades()[0].level());
        assertEquals(10, template.grades()[0].life());
        assertEquals(20, template.grades()[0].initiative());

        assertEquals(4, template.grades()[0].characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(2, template.grades()[0].characteristics().get(Characteristic.MOVEMENT_POINT));

        assertEquals(80, template.grades()[0].characteristics().get(Characteristic.STRENGTH));
        assertEquals(0, template.grades()[0].characteristics().get(Characteristic.WISDOM));
        assertEquals(80, template.grades()[0].characteristics().get(Characteristic.INTELLIGENCE));
        assertEquals(80, template.grades()[0].characteristics().get(Characteristic.LUCK));
        assertEquals(0, template.grades()[0].characteristics().get(Characteristic.AGILITY));

        assertEquals(1, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_PERCENT_NEUTRAL));
        assertEquals(5, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_PERCENT_EARTH));
        assertEquals(5, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_PERCENT_FIRE));
        assertEquals(-9, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_PERCENT_WATER));
        assertEquals(-9, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_PERCENT_AIR));
        assertEquals(5, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_ACTION_POINT));
        assertEquals(3, template.grades()[0].characteristics().get(Characteristic.RESISTANCE_MOVEMENT_POINT));

        assertEquals(2, template.grades()[0].spells().size());
        assertEquals(1, (int) template.grades()[0].spells().get(212));
        assertEquals(1, (int) template.grades()[0].spells().get(213));

        assertEquals(4, template.grades()[4].characteristics().get(Characteristic.ACTION_POINT));
        assertEquals(2, template.grades()[4].characteristics().get(Characteristic.MOVEMENT_POINT));

        assertEquals(100, template.grades()[4].characteristics().get(Characteristic.STRENGTH));
        assertEquals(0, template.grades()[4].characteristics().get(Characteristic.WISDOM));
        assertEquals(100, template.grades()[4].characteristics().get(Characteristic.INTELLIGENCE));
        assertEquals(100, template.grades()[4].characteristics().get(Characteristic.LUCK));
        assertEquals(0, template.grades()[4].characteristics().get(Characteristic.AGILITY));

        assertEquals(5, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_PERCENT_NEUTRAL));
        assertEquals(9, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_PERCENT_EARTH));
        assertEquals(9, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_PERCENT_FIRE));
        assertEquals(-5, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_PERCENT_WATER));
        assertEquals(-5, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_PERCENT_AIR));
        assertEquals(9, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_ACTION_POINT));
        assertEquals(7, template.grades()[4].characteristics().get(Characteristic.RESISTANCE_MOVEMENT_POINT));

        assertEquals(2, template.grades()[4].spells().size());
        assertEquals(5, (int) template.grades()[4].spells().get(212));
        assertEquals(5, (int) template.grades()[4].spells().get(213));
    }

    @Test
    void getByEntity() {
        MonsterTemplate template = repository.get(new MonsterTemplate(31, null, 0, null, null, null));

        assertEquals(31, template.id());
        assertEquals("Larve Bleue", template.name());
        assertEquals(1563, template.gfxId());
        assertEquals("AGGRESSIVE", template.ai());
        assertCount(5, template.grades());
    }

    @Test
    void invalidCharacteristicsLengths() throws SQLException {
        executor.query(
            "INSERT INTO `MONSTER_TEMPLATE` (`MONSTER_ID`, `MONSTER_NAME`, `GFXID`, `COLORS`, `AI`, `CHARACTERISTICS`, `LIFE_POINTS`, `INITIATIVES`, `SPELLS`) VALUES " +
                "(1, 'life invalid', 1563, '-1,-1,-1', 'AGGRESSIVE', '2@v:1;13:5;1f:5;17:-9;1b:-9;s:5;t:3;a:2g;f:2g;d:2g;8:4;9:2;|3@v:2;13:6;1f:6;17:-8;1b:-8;s:6;t:4;a:2l;f:2l;d:2l;8:4;9:2;|4@v:3;13:7;1f:7;17:-7;1b:-7;s:7;t:5;a:2q;f:2q;d:2q;8:4;9:2;|5@v:4;13:8;1f:8;17:-6;1b:-6;s:8;t:6;a:2v;f:2v;d:2v;8:4;9:2;|6@v:5;13:9;1f:9;17:-5;1b:-5;s:9;t:7;a:34;f:34;d:34;8:4;9:2;', '10|15|20|25', '20|25|35|40|50', '213@1;212@1|213@2;212@2|213@3;212@3|213@4;212@4|213@5;212@5')," +
                "(2, 'init invalid', 1563, '-1,-1,-1', 'AGGRESSIVE', '2@v:1;13:5;1f:5;17:-9;1b:-9;s:5;t:3;a:2g;f:2g;d:2g;8:4;9:2;|3@v:2;13:6;1f:6;17:-8;1b:-8;s:6;t:4;a:2l;f:2l;d:2l;8:4;9:2;|4@v:3;13:7;1f:7;17:-7;1b:-7;s:7;t:5;a:2q;f:2q;d:2q;8:4;9:2;|5@v:4;13:8;1f:8;17:-6;1b:-6;s:8;t:6;a:2v;f:2v;d:2v;8:4;9:2;|6@v:5;13:9;1f:9;17:-5;1b:-5;s:9;t:7;a:34;f:34;d:34;8:4;9:2;', '10|15|20|25|30', '20|25|35|40', '213@1;212@1|213@2;212@2|213@3;212@3|213@4;212@4|213@5;212@5')," +
                "(3, 'spells invalid', 1563, '-1,-1,-1', 'AGGRESSIVE', '2@v:1;13:5;1f:5;17:-9;1b:-9;s:5;t:3;a:2g;f:2g;d:2g;8:4;9:2;|3@v:2;13:6;1f:6;17:-8;1b:-8;s:6;t:4;a:2l;f:2l;d:2l;8:4;9:2;|4@v:3;13:7;1f:7;17:-7;1b:-7;s:7;t:5;a:2q;f:2q;d:2q;8:4;9:2;|5@v:4;13:8;1f:8;17:-6;1b:-6;s:8;t:6;a:2v;f:2v;d:2v;8:4;9:2;|6@v:5;13:9;1f:9;17:-5;1b:-5;s:9;t:7;a:34;f:34;d:34;8:4;9:2;', '10|15|20|25|30', '20|25|35|40|50', '213@1;212@1|213@2;212@2|213@3;212@3|213@4;212@4')"
        );

        assertThrows(IllegalArgumentException.class, () -> repository.get(1));
        assertThrows(IllegalArgumentException.class, () -> repository.get(2));
        assertThrows(IllegalArgumentException.class, () -> repository.get(3));
    }

    @Test
    void has() {
        assertTrue(repository.has(new MonsterTemplate(31, null, 0, null, null, null)));
        assertTrue(repository.has(new MonsterTemplate(36, null, 0, null, null, null)));
        assertFalse(repository.has(new MonsterTemplate(-5, null, 0, null, null, null)));
    }

    @Test
    void all() {
        List<MonsterTemplate> templates = repository.all();

        assertCount(3, templates);

        assertEquals(31, templates.get(0).id());
        assertEquals(34, templates.get(1).id());
        assertEquals(36, templates.get(2).id());
    }
}
