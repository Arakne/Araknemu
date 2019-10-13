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
import fr.quatrevieux.araknemu.core.network.parser.*;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.network.in.AskQueuePosition;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.handler.CheckDofusVersion;
import fr.quatrevieux.araknemu.realm.handler.StartSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RealmSessionHandlerTest extends RealmBaseCase {
    private RealmSessionHandler handler;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new RealmSessionHandler(
            new DefaultDispatcher<>(new PacketHandler[]{
                new StartSession(),
                new CheckDofusVersion(configuration),
                new PacketHandler() {
                    @Override
                    public void handle(Object session, Packet packet) {

                    }

                    @Override
                    public Class packet() {
                        return Credentials.class;
                    }
                }
            }),
            new PacketParser[] {DofusVersion.parser(), Credentials.parser()},
            new AggregatePacketParser(
                new SinglePacketParser[] {new AskQueuePosition.Parser()}
            )
        );
    }

    @Test
    void opened() throws Exception {
        handler.opened(session);

        requestStack.assertLast("HC"+session.key().key());
    }

    @Test
    void create() {
        Channel channel = new DummyChannel();
        RealmSession session = handler.create(channel);

        assertSame(channel, session.channel());
        assertNull(session.account());
        assertFalse(session.isLogged());
        assertNotNull(session.parser());
    }
}
