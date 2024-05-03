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
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractPlayingPacketHandlerTest extends GameBaseCase {
    @Test
    void withoutPlayer() {
        AtomicBoolean called = new AtomicBoolean(false);
        AbstractPlayingPacketHandler<Packet> handler = new AbstractPlayingPacketHandler<Packet>() {
            @Override
            protected void handle(GameSession session, GamePlayer player, Packet packet) throws Exception {
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
        gamePlayer();

        AtomicReference<GamePlayer> playerParam = new AtomicReference<>();
        AbstractPlayingPacketHandler<Packet> handler = new AbstractPlayingPacketHandler<Packet>() {
            @Override
            protected void handle(GameSession session, GamePlayer player, Packet packet) throws Exception {
                playerParam.set(player);
            }

            @Override
            public Class<Packet> packet() {
                return Packet.class;
            }
        };

        handler.handle(session, new Packet() {});
        assertSame(session.player(), playerParam.get());
    }
}
