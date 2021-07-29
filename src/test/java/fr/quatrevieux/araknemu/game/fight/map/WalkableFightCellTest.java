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

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.exception.FightMapException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class WalkableFightCellTest extends GameBaseCase {
    private WalkableFightCell cell;
    private FightMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        cell = new WalkableFightCell(
            map = new FightMap(container.get(MapTemplateRepository.class).get(10340)),
            container.get(MapTemplateRepository.class).get(10340).cells()[123],
            123
        );
    }

    @Test
    void initialState() {
        assertEquals(123, cell.id());
        assertTrue(cell.walkable());
        assertTrue(cell.walkableIgnoreFighter());
        assertFalse(cell.sightBlocking());
        assertFalse(cell.fighter().isPresent());
        assertSame(map, cell.map());
        assertSame(cell.coordinate(), cell.coordinate());
        assertSame(cell, cell.coordinate().cell());
    }

    @Test
    void withFighter() {
        Fighter fighter = Mockito.mock(Fighter.class);

        cell.set(fighter);

        assertSame(fighter, cell.fighter().get());
        assertFalse(cell.walkable());
        assertTrue(cell.walkableIgnoreFighter());
        assertTrue(cell.sightBlocking());
    }

    @Test
    void setAlreadySet() {
        Fighter fighter = Mockito.mock(Fighter.class);

        cell.set(fighter);

        assertThrows(FightMapException.class, () -> cell.set(fighter));
    }

    @Test
    void removeFighterNotSet() {
        assertThrows(FightMapException.class, () -> cell.removeFighter());
    }

    @Test
    void removeFighterSuccess() {
        Fighter fighter = Mockito.mock(Fighter.class);
        cell.set(fighter);

        cell.removeFighter();

        assertFalse(cell.fighter().isPresent());
    }

    @Test
    void equals() throws ContainerException {
        WalkableFightCell other = new WalkableFightCell(
            map = new FightMap(container.get(MapTemplateRepository.class).get(10340)),
            container.get(MapTemplateRepository.class).get(10340).cells()[456],
            456
        );

        assertEquals(cell, cell);
        assertNotEquals(cell, other);
    }
}
