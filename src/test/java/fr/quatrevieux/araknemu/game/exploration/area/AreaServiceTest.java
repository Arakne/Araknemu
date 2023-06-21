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

package fr.quatrevieux.araknemu.game.exploration.area;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.world.repository.environment.area.AreaRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.area.SubAreaRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.listener.player.InitializeAreas;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AreaServiceTest extends GameBaseCase {
    private AreaService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new AreaService(
            container.get(AreaRepository.class),
            container.get(SubAreaRepository.class)
        );

        dataSet
            .pushSubAreas()
            .pushAreas()
        ;
        service.preload(container.get(Logger.class));
    }

    @Test
    void list() {
        assertCount(7, service.list());
        assertSame(
            service.list(),
            service.list()
        );
    }

    @Test
    void get() {
        ExplorationSubArea area = service.get(3);

        assertEquals(3, area.id());
        assertEquals(0, area.area().id());
        assertEquals("Amakna", area.area().name());

        assertSame(area, service.get(3));
    }

    @Test
    void playerLoadedListener() throws SQLException, ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new PlayerLoaded(gamePlayer()));

        assertTrue(gamePlayer().dispatcher().has(InitializeAreas.class));
    }

    @Test
    void name() {
        assertEquals("map.area", service.name());
    }
}
