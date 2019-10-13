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

import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.handler.fight.PerformTurnAction;
import fr.quatrevieux.araknemu.game.handler.game.ValidateGameAction;
import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExploringOrFightingSwitcherTest extends FightBaseCase {
    @Test
    void handleNotFightingNorExploring() {
        PacketHandler exp = Mockito.mock(PacketHandler.class);
        PacketHandler fig = Mockito.mock(PacketHandler.class);

        ExploringOrFightingSwitcher handler = new ExploringOrFightingSwitcher<>(exp, fig);

        assertThrows(CloseImmediately.class, () -> handler.handle(session, Mockito.mock(Packet.class)));
    }

    @Test
    void handleOnFight() throws Exception {
        PacketHandler exp = Mockito.mock(PacketHandler.class);
        PacketHandler fig = Mockito.mock(PacketHandler.class);

        ExploringOrFightingSwitcher handler = new ExploringOrFightingSwitcher<>(exp, fig);
        createFight();

        Packet packet = new AskCharacterList(false);

        handler.handle(session, packet);

        Mockito.verify(fig).handle(session, packet);
        Mockito.verify(exp, Mockito.never()).handle(session, packet);
    }

    @Test
    void handleOnExploration() throws Exception {
        PacketHandler exp = Mockito.mock(PacketHandler.class);
        PacketHandler fig = Mockito.mock(PacketHandler.class);

        ExploringOrFightingSwitcher handler = new ExploringOrFightingSwitcher<>(exp, fig);

        Packet packet = new AskCharacterList(false);
        explorationPlayer();

        handler.handle(session, packet);

        Mockito.verify(exp).handle(session, packet);
        Mockito.verify(fig, Mockito.never()).handle(session, packet);
    }

    @Test
    void packet() {
        assertEquals(
            GameActionRequest.class,
            new ExploringOrFightingSwitcher<>(
                new ValidateGameAction(null),
                new PerformTurnAction()
            ).packet()
        );
    }
}