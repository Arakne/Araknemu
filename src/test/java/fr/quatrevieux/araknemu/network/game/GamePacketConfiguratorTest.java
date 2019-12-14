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

import fr.quatrevieux.araknemu.core.network.SessionClosed;
import fr.quatrevieux.araknemu.core.network.SessionCreated;
import fr.quatrevieux.araknemu.core.network.parser.Dispatcher;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GamePacketConfiguratorTest extends GameBaseCase {
    private GamePacketConfigurator configurator;
    private GameSession gameSession;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        configurator = new GamePacketConfigurator(
            container.get(Dispatcher.class),
            container.get(PacketParser.class)
        );
        ConfigurableSession session = new ConfigurableSession(channel);
        gameSession = new GameSession(session);

        configurator.configure(session, gameSession);
    }

    @Test
    void opened() throws Exception {
        gameSession.receive(new SessionCreated());

        requestStack.assertLast("HG");
    }

    @Test
    void closed() throws Exception {
        gameSession.receive(new SessionClosed());
        assertFalse(session.isLogged());
    }

    @Test
    void messageReceivedSuccess() throws Exception {
        Account account = new Account(1);
        dataSet.push(account);

        String token = container.get(TokenService.class).generate(account);

        gameSession.receive("AT" + token);

        assertTrue(gameSession.isLogged());
        assertEquals(1, gameSession.account().id());
    }
}
