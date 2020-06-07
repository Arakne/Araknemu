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

package fr.quatrevieux.araknemu.network.game.out.game.action;

import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.PathException;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.PathValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GameActionResponseTest extends GameBaseCase {
    @Test
    void generateWithOneArgument() {
        assertEquals(
            "GA3;1;45;abKebIgbf",
            new GameActionResponse("3", ActionType.MOVE, 45, "abKebIgbf").toString()
        );
    }

    @Test
    void generateWithoutArguments() {
        assertEquals(
            "GA3;1;45",
            new GameActionResponse("3", ActionType.MOVE, 45).toString()
        );
    }

    @Test
    void generateWithoutTwoArguments() {
        assertEquals(
            "GA3;1;45;aaa;bbb",
            new GameActionResponse("3", ActionType.MOVE, 45, "aaa", "bbb").toString()
        );
    }

    @Test
    void generateWithMoveAction() throws SQLException, ContainerException, PathException {
        ExplorationPlayer player = explorationPlayer();

        Move action = new Move(
            player,
            new Decoder<>(player.map()).decode("bftdgl", player.map().get(279)),
            new PathValidator[0]
        );

        action.setId(3);

        assertEquals("GA3;1;1;aexbftdgl", new GameActionResponse(action).toString());
    }

    @Test
    void noop() {
        assertEquals("GA;0", GameActionResponse.NOOP.toString());
    }

    @Test
    void generateWithActionNoArguments() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        Action action = Mockito.mock(Action.class);
        Mockito.when(action.performer()).thenReturn(player);
        Mockito.when(action.type()).thenReturn(ActionType.NONE);
        Mockito.when(action.arguments()).thenReturn(new Object[0]);

        assertEquals("GA;0;1", new GameActionResponse(action).toString());
    }

    @Test
    void generateWithActionMultipleArguments() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        Action action = Mockito.mock(Action.class);
        Mockito.when(action.performer()).thenReturn(player);
        Mockito.when(action.type()).thenReturn(ActionType.NONE);
        Mockito.when(action.arguments()).thenReturn(new Object[] {"aaa", "bbb"});

        assertEquals("GA;0;1;aaa;bbb", new GameActionResponse(action).toString());
    }
}
