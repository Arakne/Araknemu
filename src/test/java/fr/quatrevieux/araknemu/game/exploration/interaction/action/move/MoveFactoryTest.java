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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.move;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.validator.ValidateWalkable;
import fr.arakne.utils.maps.constant.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MoveFactoryTest extends GameBaseCase {
    private MoveFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        factory = new MoveFactory(new ValidateWalkable());
    }

    @Test
    void create() throws Exception {
        explorationPlayer().move(explorationPlayer().map().get(100), Direction.SOUTH_EAST);

        Action action = factory.create(explorationPlayer(), ActionType.MOVE, new String[] {"ebIgbf"});

        assertTrue(action instanceof Move);
        Move move = (Move) action;

        assertEquals(4, move.path().size());
        assertEquals(explorationPlayer().map().get(100), move.path().get(0).cell());
        assertEquals(explorationPlayer().map().get(99), move.path().get(1).cell());
        assertEquals(explorationPlayer().map().get(98), move.path().get(2).cell());
        assertEquals(explorationPlayer().map().get(69), move.path().get(3).cell());
    }

    @Test
    void createNotOnMap() throws SQLException {
        explorationPlayer().leave();

        assertThrows(IllegalArgumentException.class, () -> factory.create(explorationPlayer(), ActionType.MOVE, new String[] {"ebIgbf"}));
    }

    @Test
    void createMissingPath() throws Exception {
        explorationPlayer().move(explorationPlayer().map().get(100), Direction.SOUTH_EAST);
        assertThrows(IllegalArgumentException.class, () -> factory.create(explorationPlayer(), ActionType.MOVE, new String[] {}));
    }

    @Test
    void type() {
        assertSame(ActionType.MOVE, factory.type());
    }

    @Test
    void register() throws Exception {
        explorationPlayer().move(explorationPlayer().map().get(100), Direction.SOUTH_EAST);
        ExplorationActionRegistry registry = new ExplorationActionRegistry();
        factory.register(registry);

        assertInstanceOf(Move.class, factory.create(explorationPlayer(), ActionType.MOVE, new String[] {"ebIgbf"}));
    }
}
