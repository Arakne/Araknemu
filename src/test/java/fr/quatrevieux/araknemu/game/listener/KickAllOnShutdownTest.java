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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameService;
import fr.quatrevieux.araknemu.game.event.GameStopped;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;

class KickAllOnShutdownTest extends GameBaseCase {
    private KickAllOnShutdown listener;
    private GameService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = container.get(GameService.class);
        listener = new KickAllOnShutdown();
    }

    @Test
    void shutdownShouldSendShutdownMessageAndCloseSessions() throws SQLException {
        gamePlayer(true);

        listener.on(new GameStopped(service));

        requestStack.assertOne(ServerMessage.shutdown());
        assertFalse(session.isAlive());
        assertFalse(session.isLogged());
    }
}
