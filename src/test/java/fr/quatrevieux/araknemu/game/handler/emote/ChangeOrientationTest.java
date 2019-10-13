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

package fr.quatrevieux.araknemu.game.handler.emote;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.Restrictions;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.emote.SetOrientationRequest;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import fr.quatrevieux.araknemu.network.game.out.emote.PlayerOrientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChangeOrientationTest extends GameBaseCase {
    private ChangeOrientation handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new ChangeOrientation();
    }

    @Test
    void handleSuccess() throws Exception {
        ExplorationPlayer exploration = explorationPlayer();

        handler.handle(session, new SetOrientationRequest(Direction.WEST));

        assertEquals(Direction.WEST, exploration.orientation());

        requestStack.assertLast(new PlayerOrientation(exploration));
    }

    @Test
    void handleWithRestrictedDirection() throws Exception {
        ExplorationPlayer exploration = explorationPlayer();
        exploration.player().restrictions().unset(Restrictions.Restriction.ALLOW_MOVE_ALL_DIRECTION);

        assertErrorPacket(new Noop(), () -> handler.handle(session, new SetOrientationRequest(Direction.WEST)));
        assertEquals(Direction.SOUTH_EAST, exploration.orientation());

        handler.handle(session, new SetOrientationRequest(Direction.SOUTH_WEST));
        assertEquals(Direction.SOUTH_WEST, exploration.orientation());
    }

    @Test
    void functionalNotExploring() {
        assertThrows(CloseImmediately.class, () -> handlePacket(new SetOrientationRequest(Direction.WEST)));
    }

    @Test
    void functionalSuccess() throws Exception {
        ExplorationPlayer exploration = explorationPlayer();
        handlePacket(new SetOrientationRequest(Direction.WEST));

        assertEquals(Direction.WEST, exploration.orientation());
        requestStack.assertLast(new PlayerOrientation(exploration));
    }
}
