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

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.transformer.MonsterListTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class SqlMonsterGroupDataRepositoryTest extends GameBaseCase {
    private SqlMonsterGroupDataRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterGroups();

        repository = new SqlMonsterGroupDataRepository(
            new ConnectionPoolExecutor(app.database().get("game")),
            container.get(MonsterListTransformer.class)
        );
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        MonsterGroupData data = repository.get(1);

        assertEquals(1, data.id());
        assertEquals(4, data.maxSize());
        assertEquals(2, data.maxCount());
        assertEquals(Duration.ofMillis(30000), data.respawnTime());
        assertEquals("larves", data.comment());

        assertCount(2, data.monsters());
        assertEquals(31, data.monsters().get(0).id());
        assertEquals(new Interval(1, Integer.MAX_VALUE), data.monsters().get(0).level());
        assertEquals(1, data.monsters().get(0).rate());
        assertEquals(34, data.monsters().get(1).id());
        assertEquals(new Interval(10, 10), data.monsters().get(1).level());
        assertEquals(1, data.monsters().get(1).rate());
        assertEquals(2, data.totalRate());
        assertEquals(new Position(0, 0), data.winFightTeleport());
    }

    @Test
    void getWithWinFightTeleport() {
        assertEquals(new Position(10340, 125), repository.get(3).winFightTeleport());
    }

    @Test
    void getWithFixedTeamNumber() {
        assertFalse(repository.get(1).fixedTeamNumber());
        assertTrue(repository.get(2).fixedTeamNumber());
    }

    @Test
    void getByEntity() {
        MonsterGroupData data = repository.get(new MonsterGroupData(1, null, 0, 0, null, null, null, false));

        assertEquals(1, data.id());
        assertEquals(4, data.maxSize());
        assertEquals(2, data.maxCount());
        assertEquals(Duration.ofMillis(30000), data.respawnTime());
        assertEquals("larves", data.comment());

        assertCount(2, data.monsters());
        assertEquals(31, data.monsters().get(0).id());
        assertEquals(new Interval(1, Integer.MAX_VALUE), data.monsters().get(0).level());
        assertEquals(34, data.monsters().get(1).id());
        assertEquals(new Interval(10, 10), data.monsters().get(1).level());
    }

    @Test
    void has() {
        assertTrue(repository.has(new MonsterGroupData(1, null, 0, 0, null, null, null, false)));
        assertFalse(repository.has(new MonsterGroupData(-5, null, 0, 0, null, null, null, false)));
    }

    @Test
    void all() {
        assertArrayEquals(
            new int[] {1, 2, 3},
            repository.all().stream().mapToInt(MonsterGroupData::id).toArray()
        );
    }
}
