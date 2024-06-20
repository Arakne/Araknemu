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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.core.BootException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.connector.RealmConnector;
import fr.quatrevieux.araknemu.game.event.GameStopped;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertSame;

class GameServiceTest extends GameBaseCase {
    @Test
    void boot() throws BootException {
        PreloadableService foo = Mockito.mock(PreloadableService.class);
        Mockito.when(foo.name()).thenReturn("foo");
        PreloadableService bar = Mockito.mock(PreloadableService.class);
        Mockito.when(bar.name()).thenReturn("bar");

        List<PreloadableService> services = Arrays.asList(foo, bar);

        Logger logger = Mockito.mock(Logger.class);
        GameService service = new GameService(
            configuration,
            Mockito.mock(RealmConnector.class),
            server,
            logger,
            new DefaultListenerAggregate(),
            services,
            Collections.emptyList()
        );

        service.boot();

        Mockito.verify(foo).init(logger);
        Mockito.verify(bar).init(logger);

        Mockito.verify(foo, Mockito.never()).preload(logger);
        Mockito.verify(bar).preload(logger);

        Mockito.verify(logger).info("Starting game server {} at {}:{}", 2, "10.0.0.5", 456);
        Mockito.verify(logger).info(Mockito.eq("Game server {} started in {}ms"), Mockito.eq(2), Mockito.anyLong());
    }

    @Test
    void shutdownNotRunningShouldDoesNothing() {
        GameService service = new GameService(
            configuration,
            Mockito.mock(RealmConnector.class),
            server,
            Mockito.mock(Logger.class),
            new DefaultListenerAggregate(),
            Collections.emptyList(),
            Collections.emptyList()
        );

        service.shutdown();
    }
    @Test
    void shutdownUnit() throws BootException {
        DefaultListenerAggregate dispatcher = new DefaultListenerAggregate();

        AtomicReference<GameStopped> ref = new AtomicReference<>();
        dispatcher.add(GameStopped.class, ref::set);

        RealmConnector connector = Mockito.mock(RealmConnector.class);

        Logger logger = Mockito.mock(Logger.class);
        GameService service = new GameService(
            configuration,
            connector,
            server,
            logger,
            dispatcher,
            Collections.emptyList(),
            Collections.emptyList()
        );

        service.boot();
        service.shutdown();

        assertSame(service, ref.get().service());

        Mockito.verify(connector).updateState(2, GameHost.State.OFFLINE, false);
        Mockito.verify(logger).info("Stopping game server {}", 2);
        Mockito.verify(logger).info("Game server {} stopped", 2);
    }
}
