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
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoaderAggregate;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellAction;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellActionFactory;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.Teleport;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.listener.map.PerformCellActions;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapTriggerServiceTest extends GameBaseCase {
    private MapTriggerService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();
        service = new MapTriggerService(
            container.get(MapTriggerRepository.class),
            container.get(CellActionFactory.class)
        );
    }

    @Test
    void forMap() throws SQLException, ContainerException {
        dataSet.pushTrigger(new MapTrigger(10300, 123, 0, "13001,321", "-1"));
        dataSet.pushTrigger(new MapTrigger(10300, 456, 0, "13002,125", "-1"));
        dataSet.pushTrigger(new MapTrigger(10301, 145, 0, "13000,235", "-1"));

        Collection<CellAction> actions = service.forMap(container.get(ExplorationMapService.class).load(10300));

        assertCount(2, actions);
        assertContainsOnly(Teleport.class, actions);
        assertEquals(123, actions.toArray(new CellAction[0])[0].cell());
        assertEquals(456, actions.toArray(new CellAction[0])[1].cell());
    }

    @Test
    void listeners() throws ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        ExplorationMap map = new ExplorationMap(
            container.get(MapTemplateRepository.class).get(10300),
            new CellLoaderAggregate(new CellLoader[0]), null
        );

        dispatcher.dispatch(new MapLoaded(map));

        assertTrue(map.dispatcher().has(PerformCellActions.class));
    }

    @Test
    void preload() throws SQLException, ContainerException {
        dataSet.pushTrigger(new MapTrigger(10300, 123, 0, "13001,321", "-1"));
        dataSet.pushTrigger(new MapTrigger(10300, 456, 0, "13002,125", "-1"));
        dataSet.pushTrigger(new MapTrigger(10301, 145, 0, "13000,235", "-1"));

        Logger logger = Mockito.mock(Logger.class);

        service.preload(logger);

        Mockito.verify(logger).info("Loading map cells triggers...");
        Mockito.verify(logger).info("{} triggers loaded", 3);
    }

    @Test
    void forMapPreload() throws SQLException, ContainerException {
        dataSet.pushTrigger(new MapTrigger(10300, 123, 0, "13001,321", "-1"));
        dataSet.pushTrigger(new MapTrigger(10300, 456, 0, "13002,125", "-1"));
        dataSet.pushTrigger(new MapTrigger(10301, 145, 0, "13000,235", "-1"));

        service.preload(container.get(Logger.class));
        Collection<CellAction> actions = service.forMap(container.get(ExplorationMapService.class).load(10300));

        assertCount(2, actions);
        assertContainsOnly(Teleport.class, actions);
    }
}
