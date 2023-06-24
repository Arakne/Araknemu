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
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NpcTemplateRepositoryCacheTest extends GameBaseCase {
    private NpcTemplateRepositoryCache repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcTemplates();

        repository = new NpcTemplateRepositoryCache(
            container.get(NpcTemplateRepository.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getSame() {
        assertSame(
            repository.get(848),
            repository.get(848)
        );
    }

    @Test
    void getUsingEntity() {
        assertSame(
            repository.get(new NpcTemplate(848, 0, 0, 0, null, null, null, 0, 0, null)),
            repository.get(848)
        );
    }

    @Test
    void hasNotLoaded() {
        assertTrue(repository.has(new NpcTemplate(848, 0, 0, 0, null, null, null, 0, 0, null)));
        assertFalse(repository.has(new NpcTemplate(-1, 0, 0, 0, null, null, null, 0, 0, null)));
    }

    @Test
    void hasCached() {
        repository.get(848);
        assertTrue(repository.has(new NpcTemplate(848, 0, 0, 0, null, null, null, 0, 0, null)));
    }

    @Test
    void all() {
        Collection<NpcTemplate> templates = repository.all();

        assertCount(3, templates);

        for (NpcTemplate template : templates) {
            assertSame(template, repository.get(template));
        }
    }
}
