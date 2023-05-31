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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.exploration.interaction.map;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TeleportationTest extends GameBaseCase {
    private ExplorationMapService service;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        service = container.get(ExplorationMapService.class);
        player = explorationPlayer();
    }

    @Test
    void performOnSameMap() throws Exception {
        Teleportation teleport = teleportation(10300, 321);
        player.interactions().start(teleport);

        assertEquals(
            new Position(10300, 321),
            explorationPlayer().position()
        );

        assertFalse(explorationPlayer().interactions().busy());

        requestStack.assertLast(
            new AddSprites(
                Collections.singleton(explorationPlayer().sprite())
            )
        );
    }

    @Test
    void performOnSameMapInvalidCell() throws Exception {
        assertThrows(IllegalStateException.class, () -> start(10300, 1000));

        assertEquals(new Position(10300, 279), explorationPlayer().position());
        assertFalse(explorationPlayer().interactions().busy());
    }

    @Test
    void teleportOnOtherMap() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        requestStack.clear();
        start(10540, 321);

        assertEquals(new Position(10540, 321), player.position());

        requestStack.assertAll(
            new MapData(player.map()),
            new GameActionResponse("", ActionType.CHANGE_MAP, player.id(), "")
        );
    }

    @Test
    void teleportOnOtherMapWithCinematic() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        requestStack.clear();

        Teleportation teleportation = new Teleportation(player, new TeleportationTarget(
            service.load(10540),
            321
        ), 3);
        player.interactions().start(teleportation);

        assertEquals(new Position(10540, 321), player.position());

        requestStack.assertAll(
            new MapData(player.map()),
            new GameActionResponse("", ActionType.CHANGE_MAP, player.id(), "3")
        );
    }

    @Test
    void teleportOnOtherMapInvalidCell() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        requestStack.clear();
        assertThrows(IllegalStateException.class, () -> start(10540, 1000));

        assertEquals(new Position(10300, 279), player.position());
        assertFalse(explorationPlayer().interactions().busy());
    }

    private Teleportation teleportation(int mapId, int cellId) {
        return new Teleportation(player, new TeleportationTarget(
            service.load(mapId),
            cellId
        ));
    }

    private void start(int mapId, int cellId) {
        player.interactions().start(teleportation(mapId, cellId));
    }
}
