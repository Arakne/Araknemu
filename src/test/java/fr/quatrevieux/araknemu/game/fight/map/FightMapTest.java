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

package fr.quatrevieux.araknemu.game.fight.map;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FightMapTest extends GameBaseCase {
    private FightMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        map = new FightMap(
            container.get(MapTemplateRepository.class).get(10340)
        );
    }

    @Test
    void getters() {
        assertEquals(10340, map.id());
    }

    @Test
    void decoder() {
        assertSame(map.decoder(), map.decoder());
        assertSame(map.get(124), map.decoder().nextCellByDirection(map.get(123), Direction.EAST).get());
        assertSame(map.get(122), map.decoder().nextCellByDirection(map.get(123), Direction.WEST).get());
    }

    @Test
    void iterator() {
        Iterator<BattlefieldCell> it = map.iterator();

        assertSame(map.get(0), it.next());
        assertSame(map.get(1), it.next());
        assertSame(map.get(2), it.next());
    }

    @Test
    void getWalkable() {
        FightCell cell = map.get(123);

        assertSame(cell, map.get(123));
        assertInstanceOf(WalkableFightCell.class, cell);
        assertTrue(cell.walkable());
        assertFalse(cell.sightBlocking());
        assertEquals(123, cell.id());
        assertSame(map, cell.map());
    }

    @Test
    void getNotWalkable() {
        FightCell cell = map.get(1);

        assertSame(cell, map.get(1));
        assertInstanceOf(UnwalkableFightCell.class, cell);
        assertFalse(cell.walkable());
        assertFalse(cell.sightBlocking());
        assertEquals(1, cell.id());
        assertSame(map, cell.map());
    }

    @Test
    void startPlaces() throws ContainerException {
        assertArrayEquals(container.get(MapTemplateRepository.class).get(10340).fightPlaces()[0], map.startPlaces(0).stream().mapToInt(FightCell::id).toArray());
        assertArrayEquals(container.get(MapTemplateRepository.class).get(10340).fightPlaces()[1], map.startPlaces(1).stream().mapToInt(FightCell::id).toArray());
        assertEquals(Collections.emptyList(), map.startPlaces(2));
        assertEquals(Collections.emptyList(), map.startPlaces(10));
    }

    @Test
    void values() {
        assertEquals(17, map.dimensions().height());
        assertEquals(15, map.dimensions().width());
        assertEquals(479, map.size());
    }
}
