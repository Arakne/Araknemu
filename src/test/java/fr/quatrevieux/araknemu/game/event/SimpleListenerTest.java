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

package fr.quatrevieux.araknemu.game.event;

import fr.quatrevieux.araknemu.core.event.SimpleListener;
import fr.quatrevieux.araknemu.game.exploration.event.MapJoined;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertSame;

class SimpleListenerTest {
    @Test
    void event() {
        SimpleListener<MapJoined> listener = new SimpleListener<>(MapJoined.class, mapLoaded -> {});

        assertSame(MapJoined.class, listener.event());
    }

    @Test
    void on() {
        Consumer<Disconnected> consumer = Mockito.mock(Consumer.class);
        SimpleListener<Disconnected> listener = new SimpleListener<>(Disconnected.class, consumer);
        Disconnected evt = new Disconnected();

        listener.on(evt);

        Mockito.verify(consumer).accept(evt);
    }
}