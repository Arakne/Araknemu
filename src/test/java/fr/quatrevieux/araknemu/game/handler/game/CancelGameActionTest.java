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
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.Move;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.PathValidator;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.ValidateWalkable;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionCancel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CancelGameActionTest extends GameBaseCase {
    private CancelGameAction handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new CancelGameAction();
    }

    @Test
    void handleCancelMove() throws Exception {
        dataSet.pushMaps().pushSubAreas().pushAreas();

        ExplorationPlayer player = explorationPlayer();

        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("bftdgl", player.map().get(279)),
            new PathValidator[] {new ValidateWalkable()}
        );

        player.interactions().push(move);

        handler.handle(session, new GameActionCancel(1, "294"));

        assertFalse(player.interactions().busy());
        assertEquals(294, player.position().cell());
    }
}
