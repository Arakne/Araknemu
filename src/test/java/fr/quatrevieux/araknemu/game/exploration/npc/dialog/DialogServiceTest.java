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

package fr.quatrevieux.araknemu.game.exploration.npc.dialog;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Question;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.QuestionRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.ResponseActionRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.ParametersResolver;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.VariableResolver;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DialogServiceTest extends GameBaseCase {
    private DialogService service;
    private Logger logger;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushQuestions()
            .pushResponseActions()
        ;

        service = new DialogService(
            container.get(QuestionRepository.class),
            container.get(ResponseActionRepository.class),
            new ActionFactory[0],
            new ParametersResolver(new VariableResolver[0], container.get(Logger.class)),
            logger = Mockito.mock(Logger.class)
        );
    }

    @Test
    void preload() {
        Logger logger = Mockito.mock(Logger.class);

        service.preload(logger);

        Mockito.verify(logger).info("Loading dialogs responses...");
        Mockito.verify(logger).info("{} responses loaded", 3);

        Mockito.verify(logger).info("Loading dialogs questions...");
        Mockito.verify(logger).info("{} questions loaded", 6);
    }

    @Test
    void byIdsEmpty() {
        assertEquals(Collections.emptyList(), service.byIds(new int[0]));
    }

    @Test
    void byIds() {
        Collection<NpcQuestion> questions = service.byIds(new int[] {3596, 3786});

        assertCount(2, questions);

        NpcQuestion[] array = questions.stream().toArray(NpcQuestion[]::new);

        assertEquals(3596, array[0].id());
        assertEquals(3786, array[1].id());
    }

    @Test
    void byIdsNotFound() {
        assertEquals(Collections.emptyList(), service.byIds(new int[] {1225, 745}));
        Mockito.verify(logger).warn("NPC question not found : requested {}, actual {}", "[1225, 745]", "[]");
    }

    @Test
    void byIdsWillFilterNotFound() {
        Collection<NpcQuestion> questions = service.byIds(new int[] {3596, 7458});

        assertCount(1, questions);

        NpcQuestion[] array = questions.stream().toArray(NpcQuestion[]::new);

        assertEquals(3596, array[0].id());
        Mockito.verify(logger).warn("NPC question not found : requested {}, actual {}", "[3596, 7458]", "[3596]");
    }

    @Test
    void byIdsWillReturnSameInstance() {
        Collection<NpcQuestion> questions = service.byIds(new int[] {3596, 3786});

        NpcQuestion[] array = questions.stream().toArray(NpcQuestion[]::new);

        NpcQuestion question = service.byIds(new int[] {3786}).stream().findFirst().get();

        assertSame(question, array[1]);
    }

    @Test
    void responses() throws SQLException, ContainerException {
        NpcQuestion question = service.byIds(new int[] {3786}).stream().findFirst().get();

        Response[] responses = question.responses(explorationPlayer()).stream().toArray(Response[]::new);

        assertCount(2, responses);
        assertEquals(3323, responses[0].id());
        assertEquals(3324, responses[1].id());
    }

    @Test
    void actionFactoryNotFound() throws SQLException, ContainerException {
        dataSet.pushQuestion(new Question(1, new int[] {1}, new String[0], ""));
        dataSet.pushResponseAction(new ResponseAction(1, "not_found", ""));

        NpcQuestion question = service.byIds(new int[] {1}).stream().findFirst().get();

        assertCount(0, question.responses(explorationPlayer()));
        Mockito.verify(logger).warn("Response action {} is not supported for response {}", "not_found", 1);
    }

    @Test
    void byNpc() throws ContainerException, SQLException {
        dataSet.pushNpcs();

        assertEquals(
            service.byIds(new int[] {3593, 3588}),
            service.forNpc(dataSet.refresh(new Npc(457, 0, null, null, null)))
        );
    }

    @Test
    void name() {
        assertEquals("npc.dialog", service.name());
    }
}
