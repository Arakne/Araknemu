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

package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionFactory;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidateGameActionTest extends GameBaseCase {
    private ValidateGameAction handler;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        handler = new ValidateGameAction(
            container.get(ActionFactory.class)
        );

        map = explorationPlayer().map();
    }

    @Test
    void handleSuccessMove() throws Exception {
        handler.handle(
            session,
            new GameActionRequest(
                ActionType.MOVE.id(),
                new String[] {"bftdgl"}
            )
        );

        requestStack.assertLast(
            new GameActionResponse("1", ActionType.MOVE, explorationPlayer().id(), "aexbftdgl")
        );

        assertTrue(explorationPlayer().interactions().busy());
    }

    @Test
    void handleBadRequest() {
        assertThrows(ErrorPacket.class, () -> handler.handle(session, new GameActionRequest(ActionType.NONE.id(), new String[0])));
    }

    @Test
    void handleFailedWhenInteracting() throws SQLException, ContainerException {
        Interaction interaction = Mockito.mock(Interaction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);
        explorationPlayer().interactions().start(interaction);

        assertThrows(
            ErrorPacket.class,
            () -> handler.handle(
                session,
                new GameActionRequest(
                    ActionType.MOVE.id(),
                    new String[] {"bftdgl"}
                )
            )
        );
    }
}
