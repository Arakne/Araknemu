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

package fr.quatrevieux.araknemu.network.realm;

import fr.quatrevieux.araknemu.core.network.Channel;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class RealmSessionTest {
    @Test
    void channel() {
        Channel channel = new DummyChannel();

        assertSame(channel, new RealmSession(new ConfigurableSession(channel)).channel());
    }

    @Test
    void key() {
        RealmSession session = new RealmSession(new ConfigurableSession(new DummyChannel()));

        assertEquals(32, session.key().key().length());
        assertSame(session.key(), session.key());

        String key = session.key().key();

        assertEquals(key, session.key().key());
    }

    @Test
    void write() {
        DummyChannel channel = new DummyChannel();

        new RealmSession(new ConfigurableSession(channel)).send("my message");

        assertEquals("my message", channel.getMessages().lastElement());
    }
}
