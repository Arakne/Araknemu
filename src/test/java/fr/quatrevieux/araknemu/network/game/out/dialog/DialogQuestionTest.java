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

package fr.quatrevieux.araknemu.network.game.out.dialog;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Question;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @todo Test with parameters
 */
class DialogQuestionTest extends GameBaseCase {
    private ExplorationPlayer player;
    private DialogService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushQuestions()
            .pushResponseActions()
        ;

        service = container.get(DialogService.class);
        player = explorationPlayer();
    }

    @Test
    void generateWithoutResponses() {
        assertEquals("DQ3593", new DialogQuestion(question(3593), Collections.emptyList(), player).toString());
    }

    @Test
    void generateWithOneResponse() {
        assertEquals("DQ3596|3182", new DialogQuestion(question(3596), question(3596).responses(player), player).toString());
    }

    @Test
    void generateWithTwoResponses() {
        assertEquals("DQ3786|3323;3324", new DialogQuestion(question(3786), question(3786).responses(player), player).toString());
    }

    @Test
    void generateWithParameters() throws SQLException, ContainerException {
        dataSet.pushQuestion(new Question(1234, new int[0], new String[] {"[name]", "a2"}, ""));

        assertEquals("DQ1234;Bob,a2", new DialogQuestion(question(1234), Collections.emptyList(), player).toString());
    }

    private NpcQuestion question(int id) {
        return service.byIds(new int[] {id}).stream().findFirst().get();
    }
}
