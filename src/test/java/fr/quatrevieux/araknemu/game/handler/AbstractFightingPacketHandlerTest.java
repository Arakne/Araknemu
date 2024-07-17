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

package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractFightingPacketHandlerTest extends FightBaseCase {
    @Test
    void handleNotFighting() {
        AtomicBoolean called = new AtomicBoolean(false);
        AbstractFightingPacketHandler<Packet> handler = new AbstractFightingPacketHandler<Packet>() {
            @Override
            public void handle(GameSession session, Fight fight, PlayerFighter fighter, Packet packet) throws Exception {
                called.set(true);
            }

            @Override
            public Class<Packet> packet() {
                return Packet.class;
            }
        };

        assertThrows(ErrorPacket.class, () -> handler.handle(session, new Packet() {}));
        assertEquals(false, called.get());
    }

    @RepeatedIfExceptionsTest
    void handleSuccess() throws Exception {
        AtomicReference<PlayerFighter> fighterParam = new AtomicReference<>();
        AbstractFightingPacketHandler<Packet> handler = new AbstractFightingPacketHandler<Packet>() {
            @Override
            public void handle(GameSession session, Fight fight, PlayerFighter fighter, Packet packet) throws Exception {
                fighterParam.set(fighter);
            }

            @Override
            public Class<Packet> packet() {
                return Packet.class;
            }
        };
        createFight();

        Packet packet = new AskCharacterList(false);

        handler.handle(session, packet);
        assertSame(player.fighter(), fighterParam.get());
    }

    @RepeatedIfExceptionsTest
    void handleWithException() throws Exception {
        AbstractFightingPacketHandler<Packet> handler = new AbstractFightingPacketHandler<Packet>() {
            @Override
            public void handle(GameSession session, Fight fight, PlayerFighter fighter, Packet packet) throws Exception {
                throw new ErrorPacket("my packet");
            }

            @Override
            public Class<Packet> packet() {
                return Packet.class;
            }
        };
        createFight();

        Packet packet = new AskCharacterList(false);

        handler.handle(session, packet);

        requestStack.assertLast("my packet");
    }
}