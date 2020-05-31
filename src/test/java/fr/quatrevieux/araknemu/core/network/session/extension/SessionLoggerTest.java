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

package fr.quatrevieux.araknemu.core.network.session.extension;

import fr.quatrevieux.araknemu.core.network.SessionClosed;
import fr.quatrevieux.araknemu.core.network.SessionCreated;
import fr.quatrevieux.araknemu.core.network.parser.HandlerNotFoundException;
import fr.quatrevieux.araknemu.core.network.session.AbstractDelegatedSession;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.network.game.in.Ping;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SessionLoggerTest {
    private Session session;
    private Logger logger;

    class TestSession extends AbstractDelegatedSession {
        public TestSession(Session session) {
            super(session);
        }
    }

    @BeforeEach
    void setUp() {
        logger = Mockito.mock(Logger.class);

        SessionConfigurator<TestSession> configurator = new SessionConfigurator<>(TestSession::new);
        configurator.add(new SessionLogger.Configurator<>(logger));

        session = configurator.create(new DummyChannel());
    }

    @Test
    void receivePacket() {
        session.receive("my packet");

        Mockito.verify(logger).debug("[{}] Recv << {}", 1L, "my packet");
    }

    @Test
    void sessionClosed() {
        session.receive(new SessionClosed());

        Mockito.verify(logger).debug("[{}] Session closed", 1L);
    }

    @Test
    void sessionCreated() {
        session.receive(new SessionCreated());

        Mockito.verify(logger).debug("[{}] Session created", 1L);
    }

    @Test
    void sendPacket() {
        session.send("my packet");

        Mockito.verify(logger).debug("[{}] Send >> {}", 1L, "my packet");
    }

    @Test
    void exception() {
        Exception e = new Exception("my error");
        session.exception(e);

        Mockito.verify(logger).error("[{}] Uncaught exception", 1L, e);
    }

    @Test
    void exceptionWithPreviousCause() {
        Exception previous = new Exception("previous");
        Exception e = new Exception("my error", previous);
        session.exception(e);

        Mockito.verify(logger).error("[{}] Uncaught exception", 1L, e);
        Mockito.verify(logger).error("[{}] Cause : {}", 1L, previous);
    }

    @Test
    void exceptionHandlerNotFound() {
        session.exception(new HandlerNotFoundException(new Ping()));

        Mockito.verify(logger).warn("Cannot found handler for packet Ping");
    }
}
