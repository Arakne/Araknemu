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

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.ChangeMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangeMapTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        player = explorationPlayer();
        map = container.get(ExplorationMapService.class).load(10540);
    }

    @Test
    void dataNoCinematic() throws Exception {
        ChangeMap changeMap = new ChangeMap(player, map, 123);
        changeMap.setId(2);

        assertEquals(2, changeMap.id());
        assertSame(player, changeMap.performer());
        Assertions.assertEquals(ActionType.CHANGE_MAP, changeMap.type());
        assertArrayEquals(new Object[] {""}, changeMap.arguments());
    }

    @Test
    void dataCinematic() throws Exception {
        ChangeMap changeMap = new ChangeMap(player, map, 123, 6);
        changeMap.setId(2);

        assertEquals(2, changeMap.id());
        assertSame(player, changeMap.performer());
        assertEquals(ActionType.CHANGE_MAP, changeMap.type());
        assertArrayEquals(new Object[] {6}, changeMap.arguments());
    }

    @Test
    void startWithoutCinematic() throws Exception {
        ExplorationMap lastMap = player.map();
        ActionQueue queue = new ActionQueue();

        ChangeMap changeMap = new ChangeMap(player, map, 123);

        changeMap.start(queue);

        assertFalse(lastMap.creatures().contains(player));
        assertTrue(queue.isBusy());
        requestStack.assertLast(
            new GameActionResponse(changeMap)
        );
    }

    @Test
    void startWithCinematic() throws Exception {
        ExplorationMap lastMap = player.map();
        ActionQueue queue = new ActionQueue();

        ChangeMap changeMap = new ChangeMap(player, map, 123, 5);

        changeMap.start(queue);

        assertFalse(lastMap.creatures().contains(player));
        assertTrue(queue.isBusy());
        requestStack.assertLast(
            new GameActionResponse("1", ActionType.CHANGE_MAP, player.id(), "5")
        );
    }

    @Test
    void startWillNotChangePosition() throws Exception {
        ExplorationMap lastMap = player.map();
        Position lastPosition = player.position();
        ActionQueue queue = new ActionQueue();

        ChangeMap changeMap = new ChangeMap(player, map, 123, 5);

        changeMap.start(queue);

        assertSame(lastMap, player.map());
        assertEquals(lastPosition, player.position());
        assertTrue(queue.isBusy());
    }

    @Test
    void end() throws Exception {
        ChangeMap changeMap = new ChangeMap(player, map, 123, 5);
        changeMap.end();

        assertSame(map, player.map());
        assertEquals(new Position(10540, 123), player.position());

        requestStack.assertLast(
            new MapData(map)
        );
    }
}
