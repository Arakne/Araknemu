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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AbstractLoggedPacketHandlerTest extends GameBaseCase {
    @Test
    void notLogged() {
        AtomicBoolean called = new AtomicBoolean(false);
        AbstractLoggedPacketHandler<Packet> handler = new AbstractLoggedPacketHandler<Packet>() {
            @Override
            protected void handle(GameSession session, GameAccount account, Packet packet) throws Exception {
                called.set(true);
            }

            @Override
            public Class<Packet> packet() {
                return Packet.class;
            }
        };

        assertThrows(CloseImmediately.class, () -> handler.handle(session, new Packet() {}));
        assertFalse(called.get());
    }

    @Test
    void success() throws Exception {
        login();

        AtomicReference<GameAccount> accountParam = new AtomicReference<>();
        AbstractLoggedPacketHandler<Packet> handler = new AbstractLoggedPacketHandler<Packet>() {
            @Override
            protected void handle(GameSession session, GameAccount account, Packet packet) throws Exception {
                accountParam.set(account);
            }

            @Override
            public Class<Packet> packet() {
                return Packet.class;
            }
        };

        handler.handle(session, new Packet() {});
        assertSame(session.account(), accountParam.get());
    }
}
