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

package fr.quatrevieux.araknemu.realm;

import fr.quatrevieux.araknemu.core.BootException;
import fr.quatrevieux.araknemu.core.InitializableService;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.network.util.DummyServer;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class RealmServiceTest extends RealmBaseCase {
    @Test
    void boot() throws BootException {
        InitializableService foo = Mockito.mock(InitializableService.class);
        InitializableService bar = Mockito.mock(InitializableService.class);

        List<InitializableService> services = Arrays.asList(foo, bar);

        Logger logger = Mockito.mock(Logger.class);
        RealmService service = new RealmService(
            configuration,
            new DummyServer<>(sessionHandler),
            logger,
            new DefaultListenerAggregate(),
            services,
            Collections.emptyList()
        );

        service.boot();

        Mockito.verify(foo).init(logger);
        Mockito.verify(bar).init(logger);

        Mockito.verify(logger).info("Starting realm server on port {}", 456);
        Mockito.verify(logger).info("Realm server started");
    }
}
