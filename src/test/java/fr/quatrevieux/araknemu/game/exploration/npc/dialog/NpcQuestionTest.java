package fr.quatrevieux.araknemu.game.exploration.npc.dialog;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.QuestionRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NpcQuestionTest extends GameBaseCase {
    private QuestionRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushQuestions();
        repository = container.get(QuestionRepository.class);
    }

    @Test
    void id() {
        assertEquals(3596, new NpcQuestion(repository.get(3596), Collections.emptyList()).id());
    }

    @Test
    void parameters() throws SQLException, ContainerException {
        assertArrayEquals(new String[0], new NpcQuestion(repository.get(3596), Collections.emptyList()).parameters(explorationPlayer()));
    }

    @Test
    void check() throws SQLException, ContainerException {
        assertTrue(new NpcQuestion(repository.get(3596), Collections.emptyList()).check(explorationPlayer()));
    }

    @Test
    void responsesShouldFilterInvalid() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        List<Response> responses = new ArrayList<>();

        Action a1 = Mockito.mock(Action.class);
        Action a2 = Mockito.mock(Action.class);
        Action a3 = Mockito.mock(Action.class);

        Mockito.when(a1.check(player)).thenReturn(true);
        Mockito.when(a2.check(player)).thenReturn(false);
        Mockito.when(a3.check(player)).thenReturn(true);

        responses.add(new Response(1, Arrays.asList(a1)));
        responses.add(new Response(2, Arrays.asList(a2, a3)));

        NpcQuestion question = new NpcQuestion(repository.get(3596), responses);

        Collection<Response> actual = question.responses(player);

        assertEquals(Collections.singletonList(responses.get(0)), actual);
    }
}
