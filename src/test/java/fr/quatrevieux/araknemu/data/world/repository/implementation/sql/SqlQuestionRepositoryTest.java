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
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Question;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlQuestionRepositoryTest extends GameBaseCase {
    private SqlQuestionRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushQuestion(new Question(3596, new int[] {3182}, new String[] {}, ""));
        dataSet.pushQuestion(new Question(3786, new int[] {3323, 3324}, new String[] {}, ""));

        repository = new SqlQuestionRepository(new ConnectionPoolExecutor(app.database().get("game")));
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(-5));
    }

    @Test
    void getById() {
        Question question = repository.get(3786);

        assertEquals(3786, question.id());
        assertArrayEquals(new int[] {3323, 3324}, question.responseIds());
        assertArrayEquals(new String[] {}, question.parameters());
        assertEquals("", question.condition());
    }

    @Test
    void getByEntity() {
        Question question = repository.get(new Question(3786, null, null, null));

        assertEquals(3786, question.id());
        assertArrayEquals(new int[] {3323, 3324}, question.responseIds());
        assertArrayEquals(new String[] {}, question.parameters());
        assertEquals("", question.condition());
    }

    @Test
    void has() {
        assertTrue(repository.has(new Question(3786, null, null, null)));
        assertTrue(repository.has(new Question(3596, null, null, null)));
        assertFalse(repository.has(new Question(-5, null, null, null)));
    }

    @Test
    void all() {
        assertArrayEquals(
            new Object[] {3596, 3786},
            repository.all().stream().map(Question::id).toArray()
        );
    }

    @Test
    void byNpc() {
        assertArrayEquals(new int[] {3596, 3786}, repository.byNpc(new Npc(0, 0, null, null, new int[] {3596, 3786})).stream().mapToInt(Question::id).toArray());
        assertArrayEquals(new int[] {3786}, repository.byNpc(new Npc(0, 0, null, null, new int[] {3786})).stream().mapToInt(Question::id).toArray());
        assertArrayEquals(new int[] {}, repository.byNpc(new Npc(0, 0, null, null, new int[] {})).stream().mapToInt(Question::id).toArray());
    }

    @Test
    void byIds() {
        assertArrayEquals(new int[] {3596, 3786}, repository.byIds(new int[] {3596, 3786}).stream().mapToInt(Question::id).toArray());
        assertArrayEquals(new int[] {3786}, repository.byIds(new int[] {3786}).stream().mapToInt(Question::id).toArray());
        assertArrayEquals(new int[] {}, repository.byIds(new int[] {}).stream().mapToInt(Question::id).toArray());
    }
}
