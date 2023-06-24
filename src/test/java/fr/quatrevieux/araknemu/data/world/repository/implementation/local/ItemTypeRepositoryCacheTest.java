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
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemTypeRepositoryCacheTest extends GameBaseCase {
    private ItemTypeRepositoryCache repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        repository = new ItemTypeRepositoryCache(
            container.get(ItemTypeRepository.class)
        );

        dataSet.pushItemTypes();
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getSame() {
        assertSame(
            repository.get(7),
            repository.get(7)
        );
    }

    @Test
    void getUsingEntity() {
        assertSame(
            repository.get(new ItemType(7, null, null, null)),
            repository.get(7)
        );
    }

    @Test
    void hasNotLoaded() {
        assertTrue(repository.has(new ItemType(7, null, null, null)));
        assertFalse(repository.has(new ItemType(-5, null, null, null)));
    }

    @Test
    void hasCached() {
        repository.get(7);
        assertTrue(repository.has(new ItemType(7, null, null, null)));
    }

    @Test
    void load() {
        Collection<ItemType> templates = repository.load();

        assertCount(114, templates);

        for (ItemType template : templates) {
            assertSame(
                template,
                repository.get(template)
            );
        }
    }
}