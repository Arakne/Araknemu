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
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.handler.fight.ChangeFighterStartPlace;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnsureFightingTest extends FightBaseCase {
    @Test
    void handleNotFighting() {
        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsureFighting handler = new EnsureFighting<>(inner);

        assertThrows(CloseImmediately.class, () -> handler.handle(session, Mockito.mock(Packet.class)));
    }

    @RepeatedIfExceptionsTest
    void handleSuccess() throws Exception {
        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsureFighting handler = new EnsureFighting<>(inner);
        createFight();

        Packet packet = new AskCharacterList(false);

        handler.handle(session, packet);

        Mockito.verify(inner).handle(session, packet);
    }

    @RepeatedIfExceptionsTest
    void handleWithException() throws Exception {
        PacketHandler inner = Mockito.mock(PacketHandler.class);

        EnsureFighting handler = new EnsureFighting<>(inner);
        createFight();

        Packet packet = new AskCharacterList(false);

        Mockito.doThrow(new ErrorPacket("my packet")).when(inner).handle(session, packet);

        handler.handle(session, packet);

        requestStack.assertLast("my packet");
    }

    @Test
    void packet() {
        assertEquals(
            FighterChangePlace.class,
            new EnsureFighting<>(new ChangeFighterStartPlace()).packet()
        );
    }
}