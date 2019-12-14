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
import fr.quatrevieux.araknemu.core.network.exception.RateLimitException;
import fr.quatrevieux.araknemu.core.network.session.AbstractDelegatedSession;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.core.network.session.SessionConfigurator;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateLimiterTest {
    class TestSession extends AbstractDelegatedSession {
        public TestSession(Session session) {
            super(session);
        }
    }

    @Test
    void limitReached() {
        AtomicInteger receivedPackets = new AtomicInteger();
        AtomicInteger rateLimitExceptions = new AtomicInteger();
        SessionConfigurator<TestSession> configurator = new SessionConfigurator<>(TestSession::new);

        configurator.add(new RateLimiter.Configurator<>(3));
        configurator.add((inner, session) -> inner.addReceiveMiddleware((packet, next) -> receivedPackets.incrementAndGet()));
        configurator.add((inner, session) -> inner.addExceptionHandler(RateLimitException.class, e -> { rateLimitExceptions.incrementAndGet(); return false; }));

        TestSession session = configurator.create(new DummyChannel());

        for (int i = 0; i < 5; ++i) {
            session.receive("packet");
        }

        assertEquals(3, receivedPackets.get());
        assertEquals(2, rateLimitExceptions.get());
    }

    @Test
    void success() {
        AtomicInteger receivedPackets = new AtomicInteger();
        AtomicInteger rateLimitExceptions = new AtomicInteger();
        SessionConfigurator<TestSession> configurator = new SessionConfigurator<>(TestSession::new);

        configurator.add(new RateLimiter.Configurator<>(5));
        configurator.add((inner, session) -> inner.addReceiveMiddleware((packet, next) -> receivedPackets.incrementAndGet()));
        configurator.add((inner, session) -> inner.addExceptionHandler(RateLimitException.class, e -> { rateLimitExceptions.incrementAndGet(); return false; }));

        TestSession session = configurator.create(new DummyChannel());

        for (int i = 0; i < 5; ++i) {
            session.receive("packet");
        }

        assertEquals(5, receivedPackets.get());
        assertEquals(0, rateLimitExceptions.get());
    }

    @Test
    void successAfterSleep() throws InterruptedException {
        AtomicInteger receivedPackets = new AtomicInteger();
        AtomicInteger rateLimitExceptions = new AtomicInteger();
        SessionConfigurator<TestSession> configurator = new SessionConfigurator<>(TestSession::new);

        configurator.add(new RateLimiter.Configurator<>(5));
        configurator.add((inner, session) -> inner.addReceiveMiddleware((packet, next) -> receivedPackets.incrementAndGet()));
        configurator.add((inner, session) -> inner.addExceptionHandler(RateLimitException.class, e -> { rateLimitExceptions.incrementAndGet(); return false; }));

        TestSession session = configurator.create(new DummyChannel());

        for (int i = 0; i < 4; ++i) {
            session.receive("packet");
        }

        Thread.sleep(1000);

        for (int i = 0; i < 4; ++i) {
            session.receive("packet");
        }

        assertEquals(8, receivedPackets.get());
        assertEquals(0, rateLimitExceptions.get());
    }

    @Test
    void withInternalPacketShouldIgnore() {
        AtomicInteger receivedPackets = new AtomicInteger();
        AtomicInteger rateLimitExceptions = new AtomicInteger();
        SessionConfigurator<TestSession> configurator = new SessionConfigurator<>(TestSession::new);

        configurator.add(new RateLimiter.Configurator<>(3));
        configurator.add((inner, session) -> inner.addReceiveMiddleware((packet, next) -> receivedPackets.incrementAndGet()));
        configurator.add((inner, session) -> inner.addExceptionHandler(RateLimitException.class, e -> { rateLimitExceptions.incrementAndGet(); return false; }));

        TestSession session = configurator.create(new DummyChannel());

        for (int i = 0; i < 5; ++i) {
            session.receive(new SessionClosed());
        }

        assertEquals(5, receivedPackets.get());
        assertEquals(0, rateLimitExceptions.get());
    }
}