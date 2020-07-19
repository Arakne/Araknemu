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

package fr.quatrevieux.araknemu.game.handler.loader;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.handler.CheckQueuePosition;
import fr.quatrevieux.araknemu.game.handler.StartSession;
import fr.quatrevieux.araknemu.game.handler.StopSession;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AggregateLoaderTest extends GameBaseCase {
    @Test
    void load() throws ContainerException {
        Loader l1 = Mockito.mock(Loader.class);
        Loader l2 = Mockito.mock(Loader.class);

        Mockito.when(l1.load(container)).thenReturn(new PacketHandler[] {new StartSession(), new StopSession(Mockito.mock(Logger.class))});
        Mockito.when(l2.load(container)).thenReturn(new PacketHandler[] {new CheckQueuePosition()});

        AggregateLoader loader = new AggregateLoader(l1, l2);

        PacketHandler[] handlers = loader.load(container);

        assertCount(3, handlers);
        assertContainsType(StartSession.class, handlers);
        assertContainsType(StopSession.class, handlers);
        assertContainsType(CheckQueuePosition.class, handlers);
    }
}
