package fr.quatrevieux.araknemu.game.exploration.npc.dialog;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Question;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.QuestionRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.ParametersResolver;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.VariableResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.helpers.NOPLogger;

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
        assertEquals(3596, new NpcQuestion(repository.get(3596), Collections.emptyList(), new ParametersResolver(new VariableResolver[0], NOPLogger.NOP_LOGGER)).id());
    }

    @Test
    void parameters() throws SQLException, ContainerException {
        assertArrayEquals(new String[0], new NpcQuestion(repository.get(3596), Collections.emptyList(), container.get(ParametersResolver.class)).parameters(explorationPlayer()));
        assertArrayEquals(new String[] {"my constant"}, new NpcQuestion(new Question(123, new int[0], new String[] {"my constant"}, ""), Collections.emptyList(), container.get(ParametersResolver.class)).parameters(explorationPlayer()));
        assertArrayEquals(new String[] {"Bob"}, new NpcQuestion(new Question(123, new int[0], new String[] {"[name]"}, ""), Collections.emptyList(), container.get(ParametersResolver.class)).parameters(explorationPlayer()));
        assertArrayEquals(new String[] {"const", "Bob", "[undefined]"}, new NpcQuestion(new Question(123, new int[0], new String[] {"const", "[name]", "[undefined]"}, ""), Collections.emptyList(), container.get(ParametersResolver.class)).parameters(explorationPlayer()));
    }

    @Test
    void check() throws SQLException, ContainerException {
        assertTrue(new NpcQuestion(repository.get(3596), Collections.emptyList(), new ParametersResolver(new VariableResolver[0], NOPLogger.NOP_LOGGER)).check(explorationPlayer()));
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

        NpcQuestion question = new NpcQuestion(repository.get(3596), responses, new ParametersResolver(new VariableResolver[0], NOPLogger.NOP_LOGGER));

        Collection<Response> actual = question.responses(player);

        assertEquals(Collections.singletonList(responses.get(0)), actual);
    }
}
