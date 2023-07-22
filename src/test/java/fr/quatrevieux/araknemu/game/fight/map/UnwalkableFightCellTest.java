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

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.FightMapException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnwalkableFightCellTest extends GameBaseCase {
    private MapTemplate mapTemplate;
    private FightMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        mapTemplate = container.get(MapTemplateRepository.class).get(10340);
        map = new FightMap(mapTemplate);
    }

    @Test
    void sightBlocking() {
        UnwalkableFightCell cell = new UnwalkableFightCell(map, mapTemplate.cells()[0], 0);
        assertFalse(cell.sightBlocking());

        cell = new UnwalkableFightCell(map, mapTemplate.cells()[11], 11);
        assertTrue(cell.sightBlocking());
    }

    @Test
    void getters() {
        UnwalkableFightCell cell = new UnwalkableFightCell(map, mapTemplate.cells()[0], 0);

        assertEquals(0, cell.id());
        assertFalse(cell.walkable());
        assertFalse(cell.walkableIgnoreFighter());
        assertNull(cell.fighter());
        assertSame(map, cell.map());

        assertSame(cell.coordinate(), cell.coordinate());
        assertSame(cell, cell.coordinate().cell());
    }

    @Test
    void set() {
        UnwalkableFightCell cell = new UnwalkableFightCell(map, mapTemplate.cells()[0], 0);

        assertThrows(FightMapException.class, () -> cell.set(Mockito.mock(Fighter.class)));
    }

    @Test
    void removeFighter() {
        UnwalkableFightCell cell = new UnwalkableFightCell(map, mapTemplate.cells()[0], 0);

        assertThrows(FightMapException.class, () -> cell.removeFighter());
        cell.removeFighter(Mockito.mock(Fighter.class)); //should not throw
    }

    @Test
    void equals() {
        UnwalkableFightCell cell0 = new UnwalkableFightCell(map, mapTemplate.cells()[0], 0);
        UnwalkableFightCell cell1 = new UnwalkableFightCell(map, mapTemplate.cells()[1], 1);

        assertEquals(cell0, cell0);
        assertNotEquals(cell0, cell1);
    }
}
