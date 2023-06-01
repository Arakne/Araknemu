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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.move;

import fr.arakne.utils.maps.serializer.CellData;
import fr.arakne.utils.value.Dimensions;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.TeleportationTarget;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TeleportationTargetTest extends GameBaseCase {
    private ExplorationMapService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        service = container.get(ExplorationMapService.class);
    }

    @Test
    void withMap() {
        TeleportationTarget target = new TeleportationTarget(service.load(10340), 123);
        TeleportationTarget target2 = target.withMap(service.load(10300));

        assertNotSame(target, target2);
        assertEquals(123, target.cell());
        assertEquals(service.load(10340), target.map());
        assertEquals(123, target2.cell());
        assertEquals(service.load(10300), target2.map());
    }

    @Test
    void withCell() {
        TeleportationTarget target = new TeleportationTarget(service.load(10340), 123);
        TeleportationTarget target2 = target.withCell(456);

        assertNotSame(target, target2);
        assertEquals(123, target.cell());
        assertEquals(service.load(10340), target.map());
        assertEquals(456, target2.cell());
        assertEquals(service.load(10340), target2.map());
    }

    @Test
    void ensureCellWalkableAlreadyWalkable() {
        TeleportationTarget target = new TeleportationTarget(service.load(10340), 123);
        TeleportationTarget target2 = target.ensureCellWalkable();

        assertSame(target, target2);
        assertEquals(123, target.cell());
        assertEquals(service.load(10340), target.map());
    }

    @Test
    void ensureCellWalkableCellNotExists() {
        TeleportationTarget target = new TeleportationTarget(service.load(10340), 1000);
        TeleportationTarget target2 = target.ensureCellWalkable();

        assertNotSame(target, target2);
        assertEquals(15, target2.cell());
        assertEquals(service.load(10340), target2.map());
    }

    @Test
    void ensureCellWalkableCellNotWalkable() {
        TeleportationTarget target = new TeleportationTarget(service.load(10340), 1);
        TeleportationTarget target2 = target.ensureCellWalkable();

        assertNotSame(target, target2);
        assertEquals(15, target2.cell());
        assertEquals(service.load(10340), target2.map());
    }

    @Test
    void ensureCellWalkableInvalidMap() {
        TeleportationTarget target = new TeleportationTarget(
            new ExplorationMap(
                new MapTemplate(0, "", new Dimensions(0, 0), "", new CellData[0], new int[0][], new Geolocation(0, 0), 0, true),
                Mockito.mock(CellLoader.class),
                null
            ),
            1
        );
        assertThrows(IllegalStateException.class, target::ensureCellWalkable);
    }

    @Test
    void cellInvalid() {
        assertThrows(IllegalStateException.class, () -> new TeleportationTarget(service.load(10340), 1000).cell());
    }

    @Test
    void apply() throws SQLException {
        ExplorationPlayer player = explorationPlayer();
        requestStack.clear();

        new TeleportationTarget(service.load(10340), 123).apply(player);

        assertEquals(10340, player.map().id());
        assertEquals(123, player.cell().id());
    }
}
