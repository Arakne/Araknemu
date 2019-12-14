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

import fr.quatrevieux.araknemu.core.network.parser.*;
import fr.quatrevieux.araknemu.core.network.session.ConfigurableSession;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.network.in.AskQueuePosition;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RealmPacketParserMiddlewareTest {
    @Test
    void loginProcedure() {
        ConfigurableSession session = new ConfigurableSession(new DummyChannel());

        session.addReceiveMiddleware(new RealmPacketParserMiddleware(
            new PacketParser[] {DofusVersion.parser(), Credentials.parser()},
            new AggregatePacketParser(
                new SinglePacketParser[] {new AskQueuePosition.Parser()}
            )
        ));

        List<Object> receivedPackets = new ArrayList<>();
        session.addReceiveMiddleware((packet, next) -> receivedPackets.add(packet));

        session.receive("1.29.1");

        assertTrue(receivedPackets.get(0) instanceof DofusVersion);
        assertEquals("1.29.1", DofusVersion.class.cast(receivedPackets.get(0)).version());

        session.receive("authenticate\n#1password");

        assertTrue(receivedPackets.get(1) instanceof Credentials);
        assertEquals("authenticate", Credentials.class.cast(receivedPackets.get(1)).username());

        session.receive("Af");
        assertTrue(receivedPackets.get(2) instanceof AskQueuePosition);
    }

    @Test
    void invalidPacketDuringLogin() {
        ConfigurableSession session = new ConfigurableSession(new DummyChannel());

        session.addReceiveMiddleware(new RealmPacketParserMiddleware(
            new PacketParser[] {DofusVersion.parser(), Credentials.parser()},
            new AggregatePacketParser(
                new SinglePacketParser[] {new AskQueuePosition.Parser()}
            )
        ));

        AtomicReference<ParsePacketException> ref = new AtomicReference<>();
        session.addExceptionHandler(ParsePacketException.class, (e) -> { ref.set(e); return false; });

        List<Object> receivedPackets = new ArrayList<>();
        session.addReceiveMiddleware((packet, next) -> receivedPackets.add(packet));

        session.receive("1.29.1");
        session.receive("invalid");

        assertEquals(1, receivedPackets.size());
        assertEquals("invalid", ref.get().packet());
    }
}
