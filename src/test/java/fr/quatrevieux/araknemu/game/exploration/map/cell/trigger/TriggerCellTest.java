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

package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.Teleport;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriggerCellTest extends GameBaseCase {
    private TriggerCell cell;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        cell = new TriggerCell(
            123,
            new Teleport(
                container.get(ExplorationMapService.class),
                123,
                new Position(10340, 321)
            ),
            container.get(ExplorationMapService.class).load(10300)
        );
    }

    @Test
    void values() throws ContainerException {
        assertEquals(123, cell.id());
        assertTrue(cell.walkable());
        assertSame(container.get(ExplorationMapService.class).load(10300), cell.map());
        assertFalse(cell.free());
    }

    @Test
    void equals() {
        assertEquals(cell, cell);
        assertNotEquals(cell, new Object());
        assertNotEquals(cell, new TriggerCell(321, null, null));
    }

    @Test
    void hashCodeValue() {
        assertEquals(cell.hashCode(), cell.hashCode());
        assertNotEquals(cell.hashCode(), new TriggerCell(321, null, null).hashCode());
    }

    @Test
    void onStop() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();
        requestStack.clear();
        cell.onStop(player);

        assertEquals(321, player.cell().id());
        assertEquals(10340, player.map().id());

        requestStack.assertAll(
            new MapData(player.map()),
            new GameActionResponse("", ActionType.CHANGE_MAP, player.id(), "")
        );
    }
}
