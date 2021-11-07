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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.handler.fight.PerformTurnAction;
import fr.quatrevieux.araknemu.game.handler.game.ValidateGameAction;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class EnsureFightingOrSpectatorTest extends FightBaseCase {
    @Test
    void handleNotFightingNorSpectator() {
        PacketHandler spe = Mockito.mock(PacketHandler.class);
        PacketHandler fig = Mockito.mock(PacketHandler.class);

        EnsureFightingOrSpectator handler = new EnsureFightingOrSpectator<>(fig, spe);

        assertThrows(CloseImmediately.class, () -> handler.handle(session, Mockito.mock(Packet.class)));
    }

    @Test
    void handleOnFight() throws Exception {
        PacketHandler spe = Mockito.mock(PacketHandler.class);
        PacketHandler fig = Mockito.mock(PacketHandler.class);

        EnsureFightingOrSpectator handler = new EnsureFightingOrSpectator<>(fig, spe);
        createFight();

        Packet packet = new AskCharacterList(false);

        handler.handle(session, packet);
        Thread.sleep(100);

        Mockito.verify(fig).handle(session, packet);
        Mockito.verify(spe, Mockito.never()).handle(session, packet);
    }

    @Test
    void handleOnSpectator() throws Exception {
        PacketHandler spe = Mockito.mock(PacketHandler.class);
        PacketHandler fig = Mockito.mock(PacketHandler.class);

        EnsureFightingOrSpectator handler = new EnsureFightingOrSpectator<>(fig, spe);

        Packet packet = new AskCharacterList(false);
        gamePlayer().start(new Spectator(gamePlayer(), createSimpleFight(container.get(ExplorationMapService.class).load(10340))));

        handler.handle(session, packet);
        Thread.sleep(100);

        Mockito.verify(spe).handle(session, packet);
        Mockito.verify(fig, Mockito.never()).handle(session, packet);
    }

    @Test
    void packet() {
        assertEquals(
            GameActionRequest.class,
            new EnsureFightingOrSpectator<>(
                new ValidateGameAction(null),
                new PerformTurnAction()
            ).packet()
        );
    }
}
