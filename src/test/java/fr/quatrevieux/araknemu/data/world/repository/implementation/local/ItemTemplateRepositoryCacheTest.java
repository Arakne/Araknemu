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

package fr.quatrevieux.araknemu.data.world.repository.implementation.local;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTemplateRepositoryCacheTest extends GameBaseCase {
    private ItemTemplateRepositoryCache repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();

        repository = new ItemTemplateRepositoryCache(
            container.get(ItemTemplateRepository.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getSame() {
        assertSame(
            repository.get(40),
            repository.get(40)
        );
    }

    @Test
    void getUsingEntity() {
        assertSame(
            repository.get(new ItemTemplate(40, 0, null, 0, null, 0, null, 0, null, 0)),
            repository.get(40)
        );
    }

    @Test
    void hasNotLoaded() {
        assertTrue(repository.has(new ItemTemplate(40, 0, null, 0, null, 0, null, 0, null, 0)));
        assertFalse(repository.has(new ItemTemplate(-5, 0, null, 0, null, 0, null, 0, null, 0)));
    }

    @Test
    void hasCached() {
        repository.get(40);
        assertTrue(repository.has(new ItemTemplate(40, 0, null, 0, null, 0, null, 0, null, 0)));
    }

    @Test
    void load() {
        Collection<ItemTemplate> templates = repository.load();

        assertCount(18, templates);

        for (ItemTemplate template : templates) {
            assertSame(
                template,
                repository.get(template)
            );
        }
    }
}
