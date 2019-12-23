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

package fr.quatrevieux.araknemu.game.world.map.path;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathfinderTest extends GameBaseCase {
    private Pathfinder<ExplorationMapCell> pathfinder;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        map = container.get(ExplorationMapService.class).load(10340);
        pathfinder = new Decoder<>(map).pathfinder();
    }

    @Test
    void sameCell() {
        Path<ExplorationMapCell> path = pathfinder.findPath(map.get(123), map.get(123));

        assertCount(1, path);
        assertEquals(map.get(123), path.start());
        assertEquals(map.get(123), path.target());
    }

    @Test
    void adjacentCell() {
        Path<ExplorationMapCell> path = pathfinder.findPath(map.get(336), map.get(322));

        assertCount(2, path);
        assertEquals(map.get(336), path.start());
        assertEquals(map.get(322), path.target());
    }

    @Test
    void rectilinearMove() {
        Path<ExplorationMapCell> path = pathfinder.findPath(map.get(305), map.get(221));

        assertCount(7, path);
        assertEquals(map.get(305), path.start());
        assertEquals(map.get(291), path.get(1).cell());
        assertEquals(Direction.NORTH_EAST, path.get(1).direction());
        assertEquals(map.get(277), path.get(2).cell());
        assertEquals(Direction.NORTH_EAST, path.get(2).direction());
        assertEquals(map.get(263), path.get(3).cell());
        assertEquals(Direction.NORTH_EAST, path.get(3).direction());
        assertEquals(map.get(249), path.get(4).cell());
        assertEquals(Direction.NORTH_EAST, path.get(4).direction());
        assertEquals(map.get(235), path.get(5).cell());
        assertEquals(Direction.NORTH_EAST, path.get(5).direction());
        assertEquals(map.get(221), path.target());
    }

    @Test
    void withObstacle() {
        Path<ExplorationMapCell> path = pathfinder.findPath(map.get(169), map.get(139));

        assertArrayEquals(
            new int[] {169, 183, 168, 153, 139},
            path.stream().mapToInt(step -> step.cell().id()).toArray()
        );
    }

    @Test
    void withAllDirections() {
        Path<ExplorationMapCell> path = pathfinder
            .directions(Direction.values())
            .findPath(map.get(169), map.get(139))
        ;

        assertArrayEquals(
            new int[] {169, 168, 139},
            path.stream().mapToInt(step -> step.cell().id()).toArray()
        );
    }

    @Test
    void withTargetDistance() {
        Path<ExplorationMapCell> path = pathfinder.targetDistance(1).findPath(map.get(107), map.get(225));

        assertArrayEquals(
            new int[] {107, 122, 137, 152, 167, 181, 195, 210},
            path.stream().mapToInt(step -> step.cell().id()).toArray()
        );

        path = pathfinder.targetDistance(2).findPath(map.get(107), map.get(225));

        assertArrayEquals(
            new int[] {107, 122, 137, 152, 167, 181, 195},
            path.stream().mapToInt(step -> step.cell().id()).toArray()
        );

        path = pathfinder.targetDistance(3).findPath(map.get(107), map.get(225));

        assertArrayEquals(
            new int[] {107, 122, 137, 152, 167, 182},
            path.stream().mapToInt(step -> step.cell().id()).toArray()
        );
    }

    @Test
    void invalidPath() {
        assertThrows(PathException.class, () -> pathfinder.findPath(map.get(107), map.get(225)));
    }

    @Test
    void withCustomWeightFunction() {
        Path<ExplorationMapCell> path = pathfinder
            .cellWeightFunction(cell -> cell.id() % 2 == 0 ? 10 : 1)
            .findPath(map.get(328), map.get(384))
        ;

        assertArrayEquals(
            new int[] {328, 313, 327, 341, 356, 370, 384},
            path.stream().mapToInt(step -> step.cell().id()).toArray()
        );
    }

    @Test
    void withWalkablePredicate() {
        Path<ExplorationMapCell> path = pathfinder
            .walkablePredicate(cell -> cell.walkable() && cell.id() != 168)
            .findPath(map.get(169), map.get(139));

        assertArrayEquals(
            new int[] {169, 183, 197, 182, 167, 153, 139},
            path.stream().mapToInt(step -> step.cell().id()).toArray()
        );
    }

    @Test
    void withUnwalkableCellOnPathShouldStopOnFirstUnwalkableCell() {
        Path<ExplorationMapCell> path = pathfinder
            .walkablePredicate(cell -> true)
            .findPath(map.get(169), map.get(139));

        assertArrayEquals(
            new int[] {169},
            path.stream().mapToInt(step -> step.cell().id()).toArray()
        );
    }

    @Test
    void complexPath() {
        Path<ExplorationMapCell> path = pathfinder.findPath(map.get(336), map.get(384));

        assertArrayEquals(
            new int[] {336, 321, 307, 292, 277, 263, 249, 235, 221, 207, 193, 179, 165, 180, 195, 181, 196, 211, 226, 241, 256, 270, 284, 298, 313, 328, 342, 356, 370, 384},
            path.stream().mapToInt(step -> step.cell().id()).toArray()
        );
    }

    @Test
    void encodePath() {
        assertEquals("afqffbheZfevhcLbddhc1beadeQbfidga", pathfinder.findPath(map.get(336), map.get(384)).encode());
    }

    @Test
    void exploredCellLimit() {
        assertThrows(PathException.class, () -> pathfinder.exploredCellLimit(10).findPath(map.get(336), map.get(384)).encode());
    }
}
