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

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlNpcRepositoryTest extends GameBaseCase {
    private SqlNpcRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpc(new Npc(457, 848, new Position(10302, 220), Direction.SOUTH_EAST, new int[] {16196, 16197}));
        dataSet.pushNpc(new Npc(458, 849, new Position(10302, 293), Direction.SOUTH_EAST, new int[] {3596}));
        dataSet.pushNpc(new Npc(472, 878, new Position(10340, 82), Direction.SOUTH_EAST, new int[] {3786}));
        dataSet.pushNpc(new Npc(666, 878, new Position(10540, 82), Direction.SOUTH_EAST, new int[] {}));

        repository = new SqlNpcRepository(new ConnectionPoolExecutor(app.database().get("game")));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        Npc npc = repository.get(457);

        assertEquals(457, npc.id());
        assertEquals(848, npc.templateId());
        assertEquals(new Position(10302, 220), npc.position());
        assertEquals(Direction.SOUTH_EAST, npc.orientation());
        assertArrayEquals(new int[] {16196, 16197}, npc.questions());
    }

    @Test
    void getWithoutQuestions() {
        Npc npc = repository.get(666);

        assertArrayEquals(new int[] {}, npc.questions());
    }

    @Test
    void getByEntity() {
        Npc npc = repository.get(new Npc(457, 0, null, null, null));

        assertEquals(457, npc.id());
        assertEquals(848, npc.templateId());
        assertEquals(new Position(10302, 220), npc.position());
        assertEquals(Direction.SOUTH_EAST, npc.orientation());
    }

    @Test
    void has() {
        assertTrue(repository.has(new Npc(457, 0, null, null, null)));
        assertTrue(repository.has(new Npc(458, 0, null, null, null)));
        assertTrue(repository.has(new Npc(472, 0, null, null, null)));
        assertFalse(repository.has(new Npc(-5, 0, null, null, null)));
    }

    @Test
    void all() {
        assertArrayEquals(
            new Object[] {457, 458, 472, 666},
            repository.all().stream().map(Npc::id).toArray()
        );
    }

    @Test
    void byMapId() {
        assertArrayEquals(new Object[] {457, 458}, repository.byMapId(10302).stream().map(Npc::id).toArray());
        assertArrayEquals(new Object[] {472}, repository.byMapId(10340).stream().map(Npc::id).toArray());
        assertEquals(Collections.emptyList(), repository.byMapId(-5));
    }
}
