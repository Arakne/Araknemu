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

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.creature.ExplorationCreature;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.arakne.utils.maps.constant.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RandomCellSelectorTest extends GameBaseCase {
    private RandomCellSelector selector;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        selector = new RandomCellSelector();
        selector.setMap(map = container.get(ExplorationMapService.class).load(10340));
    }

    @Test
    void cell() {
        assertNotEquals(selector.cell(), selector.cell());
        assertTrue(selector.cell().free());

        List<ExplorationMapCell> cells = new ArrayList<>(map.fightPlaces(0));
        cells.addAll(map.fightPlaces(1));
        assertTrue(cells.contains(selector.cell()));
    }

    @Test
    void cellWithoutFreePlace() {
        for (int cellId = 0; cellId < map.size(); ++cellId) {
            map.add(new FakeCreature(map.get(cellId)));
        }

        assertThrows(IllegalStateException.class, () -> selector.cell());
    }

    class FakeCreature implements ExplorationCreature {
        final private ExplorationMapCell cell;

        public FakeCreature(ExplorationMapCell cell) {
            this.cell = cell;
        }

        @Override
        public void apply(Operation operation) {

        }

        @Override
        public Sprite sprite() {
            return null;
        }

        @Override
        public int id() {
            return 1000 + cell.id();
        }

        @Override
        public ExplorationMapCell cell() {
            return cell;
        }

        @Override
        public Direction orientation() {
            return null;
        }
    }
}
