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
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SqlMonsterGroupPositionRepositoryTest  extends GameBaseCase {
    private SqlMonsterGroupPositionRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(10340, -1, 1));
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(10340, 112, 2));
        dataSet.pushMonsterGroupPosition(new MonsterGroupPosition(10540, 112, 2));

        repository = new SqlMonsterGroupPositionRepository(new ConnectionPoolExecutor(app.database().get("game")));
    }

    @Test
    void get() {
        MonsterGroupPosition position = repository.get(new MonsterGroupPosition(10340, -1, 0));

        assertEquals(10340, position.map());
        assertEquals(-1, position.cell());
        assertEquals(1, position.groupId());
    }

    @Test
    void has() {
        assertTrue(repository.has(new MonsterGroupPosition(10340, -1, 0)));
        assertFalse(repository.has(new MonsterGroupPosition(404, -1, 0)));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new MonsterGroupPosition(10340, 111, 0)));
        assertThrows(EntityNotFoundException.class, () -> repository.get(new MonsterGroupPosition(103404, -1, 0)));
        assertThrows(EntityNotFoundException.class, () -> repository.get(new MonsterGroupPosition(404, 404, 0)));
    }

    @Test
    void byMapNoGroups() {
        assertEquals(Collections.emptyList(), repository.byMap(123));
    }

    @Test
    void byMapSingleGroup() {
        Collection<MonsterGroupPosition> positions = repository.byMap(10540);

        assertCount(1, positions);
        assertEquals(2, positions.stream().findFirst().get().groupId());
    }

    @Test
    void byMapMultipleGroups() {
        Collection<MonsterGroupPosition> positions = repository.byMap(10340);

        assertArrayEquals(
            new int[] {1, 2},
            positions.stream().mapToInt(MonsterGroupPosition::groupId).toArray()
        );
    }

    @Test
    void all() {
        Collection<MonsterGroupPosition> positions = repository.all();

        assertArrayEquals(
            new Object[] {10340, 10340, 10540},
            positions.stream().map(MonsterGroupPosition::map).toArray()
        );
        assertArrayEquals(
            new Object[] {-1, 112, 112},
            positions.stream().map(MonsterGroupPosition::cell).toArray()
        );
    }
}
