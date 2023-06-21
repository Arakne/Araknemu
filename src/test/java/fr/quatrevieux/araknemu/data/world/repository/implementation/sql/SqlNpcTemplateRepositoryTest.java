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

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlNpcTemplateRepositoryTest extends GameBaseCase {
    private SqlNpcTemplateRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcTemplate(new NpcTemplate(23, 9013, 100, 100, Gender.FEMALE, new Colors(8017470, 12288585, 16770534), "0,0,0,0,0", -1, 0, null));
        dataSet.pushNpcTemplate(new NpcTemplate(40, 9025, 100, 100, Gender.MALE, new Colors(-1, -1, -1), "0,0,0,0,0", -1, 0, null));
        dataSet.pushNpcTemplate(new NpcTemplate(878, 40, 100, 100, Gender.MALE, new Colors(8158389, 13677665, 3683117), "0,20f9,2a5,1d5e,1b9e", 4, 9092, null));

        repository = new SqlNpcTemplateRepository(new ConnectionPoolExecutor(app.database().get("game")));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        NpcTemplate template = repository.get(23);

        assertEquals(23, template.id());
        assertEquals(9013, template.gfxId());
        assertEquals(100, template.scaleX());
        assertEquals(100, template.scaleY());
        assertEquals(Gender.FEMALE, template.gender());
        assertArrayEquals(new Colors(8017470, 12288585, 16770534).toArray(), template.colors().toArray());
        assertEquals("0,0,0,0,0", template.accessories());
        assertEquals(-1, template.extraClip());
        assertEquals(0, template.customArtwork());
        assertFalse(template.storeItems().isPresent());
    }

    @Test
    void getWithStore() throws SQLException {
        dataSet.pushNpcWithStore();

        NpcTemplate template = repository.get(10001);

        assertArrayEquals(new int[] {39, 2425}, template.storeItems().get());
    }

    @Test
    void getByEntity() {
        NpcTemplate template = repository.get(new NpcTemplate(23, 0, 0, 0, null, null, null, 0, 0, null));

        assertEquals(23, template.id());
        assertEquals(9013, template.gfxId());
        assertEquals(100, template.scaleX());
        assertEquals(100, template.scaleY());
        assertEquals(Gender.FEMALE, template.gender());
        assertArrayEquals(new Colors(8017470, 12288585, 16770534).toArray(), template.colors().toArray());
        assertEquals("0,0,0,0,0", template.accessories());
        assertEquals(-1, template.extraClip());
        assertEquals(0, template.customArtwork());
    }

    @Test
    void has() {
        assertTrue(repository.has(new NpcTemplate(23, 0, 0, 0, null, null, null, 0, 0, null)));
        assertTrue(repository.has(new NpcTemplate(40, 0, 0, 0, null, null, null, 0, 0, null)));
        assertTrue(repository.has(new NpcTemplate(878, 0, 0, 0, null, null, null, 0, 0, null)));
        assertFalse(repository.has(new NpcTemplate(-5, 0, 0, 0, null, null, null, 0, 0, null)));
    }

    @Test
    void all() {
        assertArrayEquals(
            new Object[] {23, 40, 878},
            repository.all().stream().map(NpcTemplate::id).toArray()
        );
    }
}
