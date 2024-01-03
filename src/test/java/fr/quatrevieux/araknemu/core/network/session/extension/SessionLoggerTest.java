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
import org.apache.logging.log4j.MarkerManager;
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

        Mockito.verify(logger).debug(MarkerManager.getMarker("RECEIVED"), "[{}] Recv << {}", session, "my packet");
    }

    @Test
    void sessionClosed() {
        session.receive(new SessionClosed());

        Mockito.verify(logger).debug(MarkerManager.getMarker("SESSION"), "[{}] Session closed", session);
    }

    @Test
    void sessionCreated() {
        session.receive(new SessionCreated());

        Mockito.verify(logger).debug(MarkerManager.getMarker("SESSION"), "[{}] Session created", session);
    }

    @Test
    void sendPacket() {
        session.send("my packet");

        Mockito.verify(logger).debug(MarkerManager.getMarker("SENT"), "[{}] Send >> {}", session, "my packet");
    }

    @Test
    void exception() {
        Exception e = new Exception("my error");
        session.exception(e);

        Mockito.verify(logger).error(MarkerManager.getMarker("NETWORK_ERROR"), "[{}] Uncaught exception", session, e);
    }

    @Test
    void exceptionWithPreviousCause() {
        Exception previous = new Exception("previous");
        Exception e = new Exception("my error", previous);
        session.exception(e);

        Mockito.verify(logger).error(MarkerManager.getMarker("NETWORK_ERROR"), "[{}] Uncaught exception", session, e);
        Mockito.verify(logger).error(MarkerManager.getMarker("NETWORK_ERROR"), "[{}] Cause : {}", session, previous);
    }

    @Test
    void exceptionHandlerNotFound() {
        session.exception(new HandlerNotFoundException(new Ping()));

        Mockito.verify(logger).warn(MarkerManager.getMarker("NETWORK_ERROR"), "Cannot found handler for packet Ping");
    }

    @Test
    void exceptionWithPacket() {
        Exception e = new Exception("my error");
        session.exception(e, "foo");

        Mockito.verify(logger).error(MarkerManager.getMarker("NETWORK_ERROR"), "[{}; packet={}] Uncaught exception", session, "foo", e);
    }

    @Test
    void exceptionWithPacketAndPreviousCause() {
        Exception previous = new Exception("previous");
        Exception e = new Exception("my error", previous);
        session.exception(e, "foo");

        Mockito.verify(logger).error(MarkerManager.getMarker("NETWORK_ERROR"), "[{}; packet={}] Uncaught exception", session, "foo", e);
        Mockito.verify(logger).error(MarkerManager.getMarker("NETWORK_ERROR"), "[{}] Cause : {}", session, previous);
    }

    @Test
    void exceptionWithPacketHandlerNotFound() {
        session.exception(new HandlerNotFoundException(new Ping()), "foo");

        Mockito.verify(logger).warn(MarkerManager.getMarker("NETWORK_ERROR"), "Cannot found handler for packet Ping");
    }
}
