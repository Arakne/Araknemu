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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.environment;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.environment.LaunchFirework;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LaunchFireworkTest extends GameBaseCase {
    private LaunchFirework action;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new LaunchFirework(
            explorationPlayer(),
            150,
            604,
            12
        );
    }

    @Test
    void id() {
        action.setId(5);
        assertEquals(5, action.id());
    }

    @Test
    void performer() throws SQLException, ContainerException {
        assertSame(explorationPlayer(), action.performer());
    }

    @Test
    void type() {
        Assertions.assertEquals(ActionType.FIREWORK, action.type());
    }

    @Test
    void arguments() {
        assertArrayEquals(
            new Object[] {"150,604,11,8,12"},
            action.arguments()
        );
    }

    @Test
    void start() {
        ActionQueue queue = new ActionQueue();
        action.start(queue);

        requestStack.assertLast(
            new GameActionResponse(action)
        );

        assertTrue(queue.isBusy());
    }

    @Test
    void startNotOnMap() throws SQLException {
        explorationPlayer().leave();
        requestStack.clear();

        ActionQueue queue = new ActionQueue();
        action.start(queue);

        requestStack.assertEmpty();
        assertFalse(queue.isBusy());
    }

    @Test
    void cancel() {
        action.cancel(null);
    }
}
