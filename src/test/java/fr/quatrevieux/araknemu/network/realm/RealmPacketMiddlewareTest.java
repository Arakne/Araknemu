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

import fr.quatrevieux.araknemu.core.network.parser.AggregatePacketParser;
import fr.quatrevieux.araknemu.core.network.parser.PacketParser;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import fr.quatrevieux.araknemu.network.in.AskQueuePosition;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.in.DofusVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RealmPacketMiddlewareTest {
    private RealmPacketParserMiddleware middleware;

    @BeforeEach
    void setUp() {
        middleware = new RealmPacketParserMiddleware(
            new PacketParser[] {DofusVersion.parser(), Credentials.parser()},
            new AggregatePacketParser(
                new SinglePacketParser[] {new AskQueuePosition.Parser()}
            )
        );
    }

    @Test
    void loginProcedure() {
        Consumer next = Mockito.mock(Consumer.class);

        middleware.handlePacket("1.29.1", next);
        Mockito.verify(next).accept(Mockito.argThat(o -> o instanceof DofusVersion && ((DofusVersion) o).version().equals("1.29.1")));

        middleware.handlePacket("authenticate\n#1password", next);
        Mockito.verify(next).accept(Mockito.argThat(o -> o instanceof Credentials && ((Credentials) o).username().equals("authenticate")));

        middleware.handlePacket("Af", next);
        Mockito.verify(next).accept(Mockito.any(AskQueuePosition.class));
    }

    @Test
    void invalidPacketDuringLogin() {
        Consumer next = Mockito.mock(Consumer.class);

        middleware.handlePacket("1.29.1", next);
        assertThrows(ParsePacketException.class, () -> middleware.handlePacket("invalid", next));
    }
}
