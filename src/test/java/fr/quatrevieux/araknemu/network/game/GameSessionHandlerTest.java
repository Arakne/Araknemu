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

package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.exception.CloseWithPacket;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.exception.InactivityTimeout;
import fr.quatrevieux.araknemu.network.game.out.account.LoginTokenError;
import fr.quatrevieux.araknemu.network.in.Dispatcher;
import fr.quatrevieux.araknemu.network.in.PacketParser;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionHandlerTest extends GameBaseCase {
    private GameSessionHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new GameSessionHandler(
            container.get(Dispatcher.class),
            container.get(PacketParser.class)
        );
    }

    @Test
    void opened() throws Exception {
        handler.opened(session);

        requestStack.assertLast("HG");
    }

    @Test
    void closed() throws Exception {
        handler.closed(session);
        assertFalse(session.isLogged());
    }

    @Test
    void exceptionCaughtCloseSession() throws Exception {
        handler.exception(session, new CloseImmediately());

        assertFalse(session.isLogged());
    }

    @Test
    void exceptionCaughtWritePacket() throws Exception {
        handler.exception(session, new ErrorPacket(new LoginTokenError()));

        requestStack.assertLast(new LoginTokenError());
    }

    @Test
    void exceptionCaughtWriteAndClose() throws Exception {
        handler.exception(session, new CloseWithPacket(new LoginTokenError()));

        requestStack.assertLast(new LoginTokenError());
        assertFalse(session.isAlive());
    }

    @Test
    void exceptionInactivityTimeout() {
        handler.exception(session, new InactivityTimeout());

        requestStack.assertLast(ServerMessage.inactivity());
        assertFalse(session.isAlive());
    }

    @Test
    void messageReceivedSuccess() throws Exception {
        Account account = new Account(1);
        dataSet.push(account);

        String token = container.get(TokenService.class).generate(account);

        handler.received(session, "AT" + token);

        assertTrue(session.isLogged());
        assertEquals(1, session.account().id());
    }
}