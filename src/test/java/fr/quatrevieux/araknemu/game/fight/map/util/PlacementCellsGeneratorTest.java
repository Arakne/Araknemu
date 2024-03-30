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

package fr.quatrevieux.araknemu.game.fight.map.util;

import fr.arakne.utils.maps.serializer.CellData;
import fr.arakne.utils.value.Dimensions;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.area.AreaService;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoaderAggregate;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlacementCellsGeneratorTest extends GameBaseCase {
    private FightMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        map = container.get(FightService.class).map(container.get(ExplorationMapService.class).load(10340));
    }

    @Test
    void nextSimple() {
        PlacementCellsGenerator generator = new PlacementCellsGenerator(map, Arrays.asList(map.get(123), map.get(124), map.get(125)));

        assertEquals(123, generator.next().id());
        assertEquals(124, generator.next().id());
        assertEquals(125, generator.next().id());
    }

    @Test
    void nextWithNonFreeCells() {
        PlacementCellsGenerator generator = new PlacementCellsGenerator(map, Arrays.asList(map.get(123), map.get(124), map.get(125)));

        map.get(123).set(Mockito.mock(Fighter.class));

        assertEquals(124, generator.next().id());
        assertEquals(125, generator.next().id());
    }

    @Test
    void nextWithNonAvailableCells() {
        PlacementCellsGenerator generator = new PlacementCellsGenerator(map, new ArrayList<>());

        for (int i = 0; i < 100; ++i) {
            FightCell cell = generator.next();
            assertTrue(cell.walkable());
        }
    }

    @Test
    void nextWithAllAvailableCellsNotFree() {
        PlacementCellsGenerator generator = new PlacementCellsGenerator(map, Arrays.asList(map.get(123), map.get(124), map.get(125)));

        map.get(123).set(Mockito.mock(Fighter.class));
        map.get(124).set(Mockito.mock(Fighter.class));
        map.get(125).set(Mockito.mock(Fighter.class));

        FightCell cell = generator.next();

        assertTrue(cell.walkable());
        assertNotEquals(123, cell.id());
        assertNotEquals(124, cell.id());
        assertNotEquals(125, cell.id());
    }

    @Test
    void nextWithEmptyMap() {
        map = new FightMap(
            new MapTemplate(
                0,
                "",
                new Dimensions(0, 0),
                "",
                new CellData[0],
                new int[2][0],
                new Geolocation(0, 0),
                0,
                false
            )
        );

        PlacementCellsGenerator generator = new PlacementCellsGenerator(map, Collections.emptyList());

        assertNull(generator.next());
    }

    @Test
    void nextWithoutAvailableCell() {
        PlacementCellsGenerator generator = new PlacementCellsGenerator(map, Arrays.asList(map.get(123), map.get(124), map.get(125)));

        for (int i = 0; i < map.size(); ++i) {
            if (map.get(i).walkable()) {
                map.get(i).set(Mockito.mock(Fighter.class));
            }
        }

        assertNull(generator.next());
    }
}
