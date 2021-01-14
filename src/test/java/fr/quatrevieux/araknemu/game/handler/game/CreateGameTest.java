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

package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.listener.map.SendAccessories;
import fr.quatrevieux.araknemu.game.listener.player.InitializeGame;
import fr.quatrevieux.araknemu.game.listener.player.LifeRegeneration;
import fr.quatrevieux.araknemu.game.listener.player.SendMapData;
import fr.quatrevieux.araknemu.game.listener.player.exploration.LeaveExplorationForFight;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreated;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreationError;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;
import fr.quatrevieux.araknemu.network.game.out.info.StartLifeTimer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameTest extends GameBaseCase {
    private CreateGame handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        handler = new CreateGame(container.get(ExplorationService.class));

        gamePlayer(true);
        requestStack.clear();
    }

    @Test
    void handleBadGameType() throws Exception {
        try {
            handler.handle(session, new CreateGameRequest(CreateGameRequest.Type.FIGHT));

            fail("ErrorPacket should be thrown");
        } catch (ErrorPacket e) {
            assertEquals(new GameCreationError().toString(), e.packet().toString());
        }
    }

    @Test
    void handleSuccess() throws Exception {
        handler.handle(session, new CreateGameRequest(CreateGameRequest.Type.EXPLORATION));

        assertNotNull(session.exploration());

        assertTrue(session.exploration().dispatcher().has(SendAccessories.class));
        assertTrue(session.exploration().dispatcher().has(InitializeGame.class));
        assertTrue(session.exploration().dispatcher().has(SendMapData.class));
        assertTrue(session.exploration().dispatcher().has(LeaveExplorationForFight.class));

        assertTrue(gamePlayer().isExploring());
        assertSame(session.exploration(), gamePlayer().scope());

        requestStack.assertAll(
            new GameCreated(CreateGameRequest.Type.EXPLORATION),
            new Stats(gamePlayer().properties()),
            new MapData(explorationPlayer().map()),
            new StartLifeTimer(LifeRegeneration.STANDARD_LIFE_REGENERATION)
        );
    }
}
