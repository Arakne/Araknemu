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
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NpcRepositoryCacheTest extends GameBaseCase {
    private NpcRepositoryCache repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcs();

        repository = new NpcRepositoryCache(
            container.get(NpcRepository.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getSame() {
        assertSame(
            repository.get(457),
            repository.get(457)
        );
    }

    @Test
    void getUsingEntity() {
        assertSame(
            repository.get(new Npc(457, 0, null, null, null)),
            repository.get(457)
        );
    }

    @Test
    void hasNotLoaded() {
        assertTrue(repository.has(new Npc(457, 0, null, null, null)));
        assertFalse(repository.has(new Npc(-1, 0, null, null, null)));
    }

    @Test
    void hasCached() {
        repository.get(457);
        assertTrue(repository.has(new Npc(457, 0, null, null, null)));
    }

    @Test
    void all() {
        Collection<Npc> npcs = repository.all();

        assertCount(3, npcs);

        for (Npc npc : npcs) {
            assertSame(npc, repository.get(npc));
        }
    }

    @Test
    void byMapIdNotLoaded() {
        assertEquals(Collections.emptyList(), repository.byMapId(-5));

        assertEquals(Arrays.asList(repository.get(457), repository.get(458)), repository.byMapId(10302));
        assertEquals(Arrays.asList(repository.get(472)), repository.byMapId(10340));
    }

    @Test
    void byMapIdLoaded() {
        repository.all();

        assertEquals(Collections.emptyList(), repository.byMapId(-5));

        assertEquals(Arrays.asList(repository.get(457), repository.get(458)), repository.byMapId(10302));
        assertEquals(Arrays.asList(repository.get(472)), repository.byMapId(10340));
    }
}
