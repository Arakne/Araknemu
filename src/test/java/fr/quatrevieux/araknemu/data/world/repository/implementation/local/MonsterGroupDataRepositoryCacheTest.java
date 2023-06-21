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
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonsterGroupDataRepositoryCacheTest extends GameBaseCase {
    private MonsterGroupDataRepositoryCache repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterGroups();

        repository = new MonsterGroupDataRepositoryCache(
            container.get(MonsterGroupDataRepository.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getSame() {
        assertSame(
            repository.get(2),
            repository.get(2)
        );
    }

    @Test
    void getUsingEntity() {
        assertSame(
            repository.get(new MonsterGroupData(2, null, 0, 0, null, null, null, false)),
            repository.get(2)
        );
    }

    @Test
    void hasNotLoaded() {
        assertTrue(repository.has(new MonsterGroupData(2, null, 0, 0, null, null, null, false)));
        assertFalse(repository.has(new MonsterGroupData(-2, null, 0, 0, null, null, null, false)));
    }

    @Test
    void hasCached() {
        repository.get(2);
        assertTrue(repository.has(new MonsterGroupData(2, null, 0, 0, null, null, null, false)));
    }

    @Test
    void all() {
        List<MonsterGroupData> groups = repository.all();

        assertCount(3, groups);

        for (MonsterGroupData template : groups) {
            assertSame(
                template,
                repository.get(template)
            );
        }
    }
}
