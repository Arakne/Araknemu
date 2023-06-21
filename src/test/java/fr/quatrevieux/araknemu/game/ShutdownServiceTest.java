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

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.ShutdownScheduled;
import fr.quatrevieux.araknemu.network.out.ServerMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShutdownServiceTest extends GameBaseCase {
    private ShutdownService service;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new ShutdownService(
            app,
            dispatcher = new DefaultListenerAggregate(),
            configuration
        );
        app.boot();
        app.add(container.get(GameService.class));
        container.get(ListenerAggregate.class).register(service);
    }

    @Override
    @AfterEach
    public void tearDown() throws fr.quatrevieux.araknemu.core.di.ContainerException {
        try {
            super.tearDown();
        } catch (RepositoryException e) {
            // Ignore repository exceptions caused by connection closed
        }
    }

    @Test
    void now() throws InterruptedException {
        service.now();

        assertFalse(app.started());
    }

    @Test
    void nowWithScheduled() {
        service.schedule(Duration.ofSeconds(1));
        assertThrows(IllegalStateException.class, service::now);
        service.cancel();
    }

    @Test
    void schedule() throws InterruptedException {
        AtomicReference<ShutdownScheduled> ref = new AtomicReference<>();
        dispatcher.add(ShutdownScheduled.class, ref::set);

        service.schedule(Duration.ofMillis(100));
        assertTrue(app.started());
        assertTrue(service.delay().isPresent());
        assertTrue(service.delay().get().toMillis() > 50);
        assertTrue(service.delay().get().toMillis() <= 100);
        assertNotNull(ref.get());
        assertBetween(80, 100, ref.get().delay().toMillis());

        Thread.sleep(200);
        assertFalse(app.started());
    }

    @Test
    void scheduleAlreadyScheduled() {
        service.schedule(Duration.ofSeconds(1));
        assertThrows(IllegalStateException.class, () -> service.schedule(Duration.ofMillis(100)));
        service.cancel();
    }

    @Test
    void cancel() {
        service.schedule(Duration.ofSeconds(1));
        assertTrue(service.cancel());
        assertFalse(service.delay().isPresent());
        assertFalse(service.cancel());
    }

    @Test
    void shutdownShouldSendShutdownMessageAndCloseSessions() throws SQLException, InterruptedException {
        gamePlayer(true);
        service.now();

        requestStack.assertOne(ServerMessage.shutdown());
        assertFalse(session.isAlive());
        assertFalse(session.isLogged());
    }
}
