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

package fr.quatrevieux.araknemu.core.network.parser;

import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultDispatcherTest {
    public static class MyPacket implements Packet {}
    public static class MyHandler implements PacketHandler<RealmSession, MyPacket> {
        public RealmSession session;
        public MyPacket packet;

        @Override
        public void handle(RealmSession session, MyPacket packet) {
            this.session = session;
            this.packet = packet;
        }

        @Override
        public Class<MyPacket> packet() {
            return MyPacket.class;
        }
    }

    @Test
    void dispatchFound() throws Exception {
        RealmSession session = new RealmSession(new ConfigurableSession(new DummyChannel()));
        MyPacket packet = new MyPacket();
        MyHandler handler = new MyHandler();

        DefaultDispatcher<RealmSession> dispatcher = new DefaultDispatcher<>(new MyHandler[]{handler});

        dispatcher.dispatch(session, packet);

        assertSame(session, handler.session);
        assertSame(packet, handler.packet);
    }

    @Test
    void dispatchNotFound() {
        DefaultDispatcher<RealmSession> dispatcher = new DefaultDispatcher<>(new MyHandler[]{});

        assertThrows(HandlerNotFoundException.class, () -> dispatcher.dispatch(new RealmSession(new ConfigurableSession(new DummyChannel())), new MyPacket()));
    }
}
