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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.spell.effect.area;

import fr.arakne.utils.maps.CoordinateCell;
import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest extends GameBaseCase {
    private ExplorationMap map;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMaps()
            .pushAreas()
            .pushSubAreas()
        ;

        map = container.get(ExplorationMapService.class).load(10340);
    }

    @Test
    void orderedSet() {
        assertOrder(
            map.get(124),
            new int[]{94, 95, 96, 109, 110, 123, 124, 125, 138, 139, 152, 154},
            new int[] {124, 110, 139, 138, 109, 96, 125, 154, 152, 123, 94, 95}
        );

        assertOrder(
            map.get(124),
            new int[]{124, 96, 110},
            new int[]{124, 110, 96}
        );

        assertOrder(
            map.get(124),
            new int[]{94, 109, 124, 139, 154},
            new int[]{124, 139, 109, 154, 94}
        );

        assertOrder(
            map.get(124),
            new int[]{94, 96, 109, 110, 124, 138, 139, 152, 154},
            new int[]{124, 110, 139, 138, 109, 96, 154, 152, 94}
        );

        assertOrder(
            map.get(124),
            new int[]{64, 65, 66, 67, 68, 93, 97, 122, 126, 151, 155, 180, 181, 182, 183, 184},
            new int[]{68, 97, 126, 155, 184, 183, 182, 181, 180, 151, 122, 93, 64, 65, 66, 67}
        );

        assertOrder(
            map.get(124),
            new int[]{79, 80, 81, 82, 108, 109, 110, 111, 137, 138, 139, 140, 166, 167, 168, 169},
            new int[]{110, 139, 138, 109, 82, 111, 140, 169, 168, 167, 166, 137, 108, 79, 80, 81}
        );
    }

    @Test
    void resolveCenterAndAdjacent() {
        assertEquals(
            new ArrayList<>(Util.resolveCenterAndAdjacent(map.get(197))),
            Arrays.asList(
                map.get(197),
                map.get(183),
                map.get(212),
                map.get(211),
                map.get(182)
            )
        );

        assertEquals(
            new ArrayList<>(Util.resolveCenterAndAdjacent(map.get(0))),
            Arrays.asList(
                map.get(0),
                map.get(15),
                map.get(14)
            )
        );
    }

    @Test
    void fullDirection() {
        assertEquals(Direction.NORTH_EAST, fullDirection(map.get(167).coordinate(), map.get(153).coordinate()));
        assertEquals(Direction.EAST, fullDirection(map.get(167).coordinate(), map.get(168).coordinate()));
        assertEquals(Direction.SOUTH_EAST, fullDirection(map.get(167).coordinate(), map.get(182).coordinate()));
        assertEquals(Direction.SOUTH, fullDirection(map.get(167).coordinate(), map.get(196).coordinate()));
        assertEquals(Direction.SOUTH_WEST, fullDirection(map.get(167).coordinate(), map.get(181).coordinate()));
        assertEquals(Direction.WEST, fullDirection(map.get(167).coordinate(), map.get(166).coordinate()));
        assertEquals(Direction.NORTH_WEST, fullDirection(map.get(167).coordinate(), map.get(152).coordinate()));
        assertEquals(Direction.NORTH, fullDirection(map.get(167).coordinate(), map.get(138).coordinate()));

        assertEquals(Direction.NORTH_EAST, fullDirection(map.get(167).coordinate(), map.get(139).coordinate()));
        assertEquals(Direction.EAST, fullDirection(map.get(167).coordinate(), map.get(169).coordinate()));
        assertEquals(Direction.SOUTH_EAST, fullDirection(map.get(167).coordinate(), map.get(197).coordinate()));
        assertEquals(Direction.SOUTH, fullDirection(map.get(167).coordinate(), map.get(312).coordinate()));
        assertEquals(Direction.SOUTH_WEST, fullDirection(map.get(167).coordinate(), map.get(195).coordinate()));
        assertEquals(Direction.WEST, fullDirection(map.get(167).coordinate(), map.get(165).coordinate()));
        assertEquals(Direction.NORTH_WEST, fullDirection(map.get(167).coordinate(), map.get(107).coordinate()));
        assertEquals(Direction.NORTH, fullDirection(map.get(167).coordinate(), map.get(109).coordinate()));

        assertEquals(Direction.NORTH_EAST, fullDirection(map.get(167).coordinate(), map.get(126).coordinate()));
        assertEquals(Direction.EAST, fullDirection(map.get(167).coordinate(), map.get(184).coordinate()));
        assertEquals(Direction.SOUTH_EAST, fullDirection(map.get(167).coordinate(), map.get(226).coordinate()));
        assertEquals(Direction.SOUTH, fullDirection(map.get(167).coordinate(), map.get(210).coordinate()));
        assertEquals(Direction.SOUTH_WEST, fullDirection(map.get(167).coordinate(), map.get(180).coordinate()));
        assertEquals(Direction.WEST, fullDirection(map.get(167).coordinate(), map.get(150).coordinate()));
        assertEquals(Direction.NORTH_WEST, fullDirection(map.get(167).coordinate(), map.get(108).coordinate()));
        assertEquals(Direction.NORTH, fullDirection(map.get(167).coordinate(), map.get(124).coordinate()));
    }

    private Direction fullDirection(CoordinateCell<?> from, CoordinateCell<?> target){
        try {
            Method method = Util.class.getDeclaredMethod("fullDirection", CoordinateCell.class, CoordinateCell.class);
            method.setAccessible(true);

            return (Direction) method.invoke(null, from, target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void assertOrder(MapCell center, int[] cellIds, int[] orderedCells) {
        Set<MapCell> cells = Util.createOrderedSet(center);

        for (int cellId : cellIds) {
            cells.add(map.get(cellId));
        }

        assertEquals(
            new ArrayList<>(cells),
            Arrays.stream(orderedCells).mapToObj(map::get).collect(Collectors.toList())
        );
    }
}
