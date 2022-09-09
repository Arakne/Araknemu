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

package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.game.exploration.npc.exchange.GameNpcExchange;
import fr.quatrevieux.araknemu.game.exploration.npc.exchange.NpcExchangeService;
import fr.quatrevieux.araknemu.game.exploration.npc.store.NpcStore;
import fr.quatrevieux.araknemu.game.exploration.npc.store.NpcStoreService;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.arakne.utils.maps.constant.Direction;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class NpcServiceTest extends GameBaseCase {
    private NpcService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        service = new NpcService(
            container.get(DialogService.class),
            container.get(NpcTemplateRepository.class),
            container.get(NpcRepository.class),
            Arrays.asList(
                container.get(NpcStoreService.class),
                container.get(NpcExchangeService.class)
            )
        );
    }

    @Test
    void listenersOnMapLoadedShouldAddNpc() throws ContainerException, SQLException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        // Push NPC after load map to ensure that old NpcService will not load NPCs
        dataSet.pushNpcs();

        dispatcher.dispatch(new MapLoaded(map));

        Creature creature = map.creature(-47204);

        assertInstanceOf(GameNpc.class, creature);

        GameNpc npc = (GameNpc) creature;

        assertEquals(map.get(82), npc.cell());
        assertEquals(Direction.SOUTH_EAST, npc.orientation());
        assertEquals(-47204, npc.id());
        assertEquals(878, npc.template().id());
    }

    @Test
    void listenersOnMapLoadedPreloaded() throws ContainerException, SQLException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        // Push NPC after load map to ensure that old NpcService will not load NPCs
        dataSet.pushNpcs();
        service.preload(container.get(Logger.class));

        dispatcher.dispatch(new MapLoaded(map));

        Creature creature = map.creature(-47204);

        assertInstanceOf(GameNpc.class, creature);

        GameNpc npc = (GameNpc) creature;

        assertEquals(878, npc.template().id());
    }

    @Test
    void preload() throws SQLException, ContainerException {
        Logger logger = Mockito.mock(Logger.class);

        dataSet.pushNpcs();
        service.preload(logger);

        Mockito.verify(logger).info("Loading NPCs...");
        Mockito.verify(logger).info("{} NPC templates loaded", 3);
        Mockito.verify(logger).info("{} NPCs loaded", 3);
    }

    @Test
    void get() throws SQLException, ContainerException {
        dataSet.pushNpcs();

        GameNpc npc = service.get(472);

        assertEquals(-47204, npc.id());
        assertEquals(82, npc.position().cell());

        NpcQuestion question = npc.question(explorationPlayer()).get();

        assertEquals(3786, question.id());
        assertCount(2, question.responses(explorationPlayer()));
    }

    @Test
    void getWithStore() throws SQLException, ContainerException {
        dataSet.pushNpcWithStore();

        GameNpc npc = service.get(10001);

        NpcStore store = (NpcStore) npc.exchangeFactory(ExchangeType.NPC_STORE);

        assertTrue(store.has(39));
        assertTrue(store.has(2425));
    }

    @Test
    void getWithExchange() throws SQLException, ContainerException {
        dataSet
            .pushNpcs()
            .pushItemTemplates()
            .pushItemSets()
            .pushNpcExchange(1, 878, 100, "39:2", 10, "2422")
        ;

        GameNpc npc = service.get(472);

        assertInstanceOf(GameNpcExchange.class, npc.exchangeFactory(ExchangeType.NPC_EXCHANGE));
    }

    @Test
    void name() {
        assertEquals("npc", service.name());
    }
}
