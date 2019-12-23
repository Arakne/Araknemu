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

package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class TriggerLoaderTest extends GameBaseCase {
    private TriggerLoader loader;
    private ExplorationMapService mapService;
    private MapTemplateRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        loader = new TriggerLoader(container.get(MapTriggerService.class));
        mapService = container.get(ExplorationMapService.class);
        repository = container.get(MapTemplateRepository.class);
    }

    @Test
    void loadWithoutTriggers() {
        assertCount(0, loader.load(mapService.load(10300), repository.get(10300).cells()));
    }

    @Test
    void loadWithTriggers() throws SQLException, ContainerException {
        dataSet.pushTrigger(new MapTrigger(10300, 123, 0, "13001,321", "-1"));
        dataSet.pushTrigger(new MapTrigger(10300, 456, 0, "13002,125", "-1"));

        Collection<ExplorationMapCell> cells = loader.load(mapService.load(10300), repository.get(10300).cells());

        assertCount(2, cells);
        assertContainsOnly(TriggerCell.class, cells);
        ExplorationMapCell[] cellsArray = cells.toArray(new ExplorationMapCell[0]);

        assertEquals(123, cellsArray[0].id());
        assertEquals(456, cellsArray[1].id());

        assertSame(mapService.load(10300), cellsArray[0].map());
    }
}