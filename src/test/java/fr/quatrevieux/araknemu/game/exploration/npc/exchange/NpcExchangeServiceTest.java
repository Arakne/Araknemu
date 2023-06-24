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

package fr.quatrevieux.araknemu.game.exploration.npc.exchange;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcExchange;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcExchangeRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NpcExchangeServiceTest extends GameBaseCase {
    private NpcExchangeService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
            .pushNpcs()
            .use(NpcExchange.class)
        ;

        service = new NpcExchangeService(
            container.get(ItemService.class),
            container.get(NpcExchangeRepository.class),
            container.get(ItemTemplateRepository.class)
        );
    }

    @Test
    void loadExchangeNotAvailable() {
        NpcTemplate template = container.get(NpcTemplateRepository.class).get(848);

        assertFalse(service.load(template).isPresent());
    }

    @Test
    void loadSuccess() throws SQLException {
        dataSet.pushNpcExchange(1, 878, 100, "39:2", 0, "2422");

        NpcTemplate template = container.get(NpcTemplateRepository.class).get(878);

        assertTrue(service.load(template).isPresent());
        assertSame(service.load(template).get(), service.load(template).get());
    }

    @Test
    void preload() throws SQLException {
        dataSet.pushNpcExchange(1, 878, 100, "39:2", 0, "2422");
        dataSet.pushNpcExchange(2, 878, 0, "2411", 1000, "2414;2425:2");
        dataSet.pushNpcExchange(3, 848, 0, "39:2", 0, "2425");
        dataSet.pushNpcExchange(4, 849, 10, "2411", 1000, "2414;2425:2");

        Logger logger = Mockito.mock(Logger.class);

        service.preload(logger);

        Mockito.verify(logger).info("Loading npc exchanges...");
        Mockito.verify(logger).info("{} npc exchanges loaded", 3);

        assertTrue(service.load(container.get(NpcTemplateRepository.class).get(878)).isPresent());
    }

    @Test
    void name() {
        assertEquals("npc.exchange", service.name());
    }
}
