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

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.exception.CloseWithPacket;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.core.network.exception.RateLimitException;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.out.account.LoginTokenError;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;

class GameExceptionConfiguratorTest extends GameBaseCase {
    private GameExceptionConfigurator configurator;
    private GameSession gameSession;
    private Logger logger;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        configurator = new GameExceptionConfigurator(logger = Mockito.mock(Logger.class));
        ConfigurableSession session = new ConfigurableSession(channel);
        gameSession = new GameSession(session);

        configurator.configure(session, gameSession);
    }

    @Test
    void exceptionCaughtCloseSession() {
        gameSession.exception(new CloseImmediately());

        assertFalse(session.isLogged());
    }

    @Test
    void exceptionCaughtWritePacket() {
        gameSession.exception(new ErrorPacket(new LoginTokenError()));

        requestStack.assertLast(new LoginTokenError());
    }

    @Test
    void exceptionCaughtWriteAndClose() {
        gameSession.exception(new CloseWithPacket(new LoginTokenError()));

        requestStack.assertLast(new LoginTokenError());
        assertFalse(session.isAlive());
    }

    @Test
    void exceptionCaughtRateLimit() {
        gameSession.exception(new RateLimitException());

        assertFalse(session.isAlive());
        Mockito.verify(logger).error(MarkerManager.getMarker("RATE_LIMIT"), "[{}] RateLimit : close session", gameSession);
    }
}
