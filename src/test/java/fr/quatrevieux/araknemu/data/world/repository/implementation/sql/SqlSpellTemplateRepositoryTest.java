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

import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.data.world.transformer.SpellTemplateLevelTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlSpellTemplateRepositoryTest extends GameBaseCase {
    private SqlSpellTemplateRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushSpells();

        repository = new SqlSpellTemplateRepository(
            new ConnectionPoolExecutor(app.database().get("game")),
            container.get(SpellTemplateLevelTransformer.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        SpellTemplate spell = repository.get(2);

        assertEquals(2, spell.id());
        assertEquals("Aveuglement", spell.name());
        assertEquals(102, spell.sprite());
        assertEquals("11,1,1", spell.spriteArgs());
        assertCount(6, spell.levels());
        assertArrayEquals(new int[0], spell.targets());
    }

    @Test
    void getByTemplate() {
        SpellTemplate spell = repository.get(new SpellTemplate(2, null, 0, null, null, null));

        assertEquals(2, spell.id());
        assertEquals("Aveuglement", spell.name());
        assertEquals(102, spell.sprite());
        assertEquals("11,1,1", spell.spriteArgs());
        assertCount(6, spell.levels());
        assertArrayEquals(new int[0], spell.targets());
    }

    @Test
    void has() {
        assertTrue(repository.has(new SpellTemplate(3, null, 0, null, null, null)));
        assertFalse(repository.has(new SpellTemplate(-3, null, 0, null, null, null)));
    }

    @Test
    void load() {
        assertCount(5, repository.load());
    }
}