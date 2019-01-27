package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Question;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionRepositoryTest extends GameBaseCase {
    private QuestionRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushQuestion(new Question(3596, new int[] {3182}, new String[] {}, ""));
        dataSet.pushQuestion(new Question(3786, new int[] {3323, 3324}, new String[] {}, ""));

        repository = new QuestionRepository(app.database().get("game"));
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
