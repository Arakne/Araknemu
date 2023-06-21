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
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseTest extends GameBaseCase {
    @Test
    void id() {
        assertEquals(123, new Response(123, new ArrayList<>()).id());
    }

    @Test
    void checkWithoutAction() throws SQLException, ContainerException {
        assertFalse(new Response(123, new ArrayList<>()).check(explorationPlayer()));
    }

    @Test
    void checkInvalid() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        Action a1 = Mockito.mock(Action.class);
        Action a2 = Mockito.mock(Action.class);

        Mockito.when(a1.check(player)).thenReturn(true);
        Mockito.when(a2.check(player)).thenReturn(false);

        assertFalse(new Response(123, Arrays.asList(a1, a2)).check(explorationPlayer()));
    }

    @Test
    void checkSuccess() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        Action a1 = Mockito.mock(Action.class);
        Action a2 = Mockito.mock(Action.class);

        Mockito.when(a1.check(player)).thenReturn(true);
        Mockito.when(a2.check(player)).thenReturn(true);

        assertTrue(new Response(123, Arrays.asList(a1, a2)).check(explorationPlayer()));
    }

    @Test
    void apply() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        Action a1 = Mockito.mock(Action.class);
        Action a2 = Mockito.mock(Action.class);

        new Response(123, Arrays.asList(a1, a2)).apply(player);

        Mockito.verify(a1).apply(player);
        Mockito.verify(a2).apply(player);
    }
}
