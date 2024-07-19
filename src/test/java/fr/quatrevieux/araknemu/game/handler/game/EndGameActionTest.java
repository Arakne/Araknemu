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

import fr.arakne.utils.maps.path.Decoder;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.PathValidator;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.ValidateWalkable;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EndGameActionTest extends GameBaseCase {
    private EndGameAction handler;
    private Logger logger;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        logger = Mockito.mock(Logger.class);
        handler = new EndGameAction(logger);
    }

    @Test
    void handleSuccess() throws Exception {
        dataSet.pushMaps().pushSubAreas().pushAreas();

        ExplorationPlayer player = explorationPlayer();

        player.interactions().push(
            new Move(
                player,
                new Decoder<>(player.map()).decode("bftdgl", player.map().get(279)),
                new PathValidator[] {new ValidateWalkable()}
            )
        );

        handler.handle(session, new GameActionAcknowledge(1));

        assertFalse(player.interactions().busy());
        assertEquals(395, player.position().cell());
    }

    @Test
    void handleIdDoesNotCorresponds() throws Exception {
        dataSet.pushMaps().pushSubAreas().pushAreas();

        ExplorationPlayer player = explorationPlayer();

        player.interactions().push(
            new Move(
                player,
                new Decoder<>(player.map()).decode("bftdgl", player.map().get(279)),
                new PathValidator[] {new ValidateWalkable()}
            )
        );

        handler.handle(session, new GameActionAcknowledge(404));
        requestStack.assertLast(new Noop());

        assertTrue(player.interactions().busy());
        Mockito.verify(logger).warn("Failed to end game action {}", 404);
    }

    @Test
    void handleWithoutPendingAction() throws Exception {
        dataSet.pushMaps().pushSubAreas().pushAreas();
        explorationPlayer();

        handler.handle(session, new GameActionAcknowledge(404));
        requestStack.assertLast(new Noop());

        Mockito.verify(logger).warn("Failed to end game action {}", 404);
    }
}
