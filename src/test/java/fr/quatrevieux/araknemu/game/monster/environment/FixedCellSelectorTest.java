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

package fr.quatrevieux.araknemu.game.monster.environment;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FixedCellSelectorTest extends GameBaseCase {
    private FixedCellSelector selector;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        selector = new FixedCellSelector(123);
    }

    @Test
    void cell() {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        selector.setMap(map);

        assertEquals(map.get(123), selector.cell());
    }

    @Test
    void cellWithoutMap() {
        assertThrows(IllegalStateException.class, selector::cell);
    }

    @Test
    void cellInvalid() {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        selector = new FixedCellSelector(1000);
        selector.setMap(map);

        assertThrows(IllegalStateException.class, selector::cell);
    }
}
