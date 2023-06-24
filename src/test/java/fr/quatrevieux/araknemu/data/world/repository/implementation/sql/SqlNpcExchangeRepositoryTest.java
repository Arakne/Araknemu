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

package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcExchange;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.transformer.ExchangeItemsTransformer;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlNpcExchangeRepositoryTest extends GameBaseCase {
    private SqlNpcExchangeRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcExchange(1, 878, 100, "39:2", 10, "2422");
        dataSet.pushNpcExchange(2, 878, 0, "2411", 1000, "2414;2425:2");
        dataSet.pushNpcExchange(3, 848, 0, "2411", 0, "2414:2");


        repository = new SqlNpcExchangeRepository(
            new ConnectionPoolExecutor(app.database().get("game")),
            container.get(ExchangeItemsTransformer.class)
        );
    }

    @Test
    void get() {
        NpcExchange entity = repository.get(new NpcExchange(1, 0, 0, null, 0, null));

        assertEquals(1, entity.id());
        assertEquals(878, entity.npcTemplateId());

        assertEquals(new HashMap<Integer, Integer>() {{ put(39, 2); }}, entity.requiredItems());
        assertEquals(100, entity.requiredKamas());

        assertEquals(new HashMap<Integer, Integer>() {{ put(2422, 1); }}, entity.exchangedItems());
        assertEquals(10, entity.exchangedKamas());
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> repository.get(new NpcExchange(404, 0, 0, null, 0, null)));
    }

    @Test
    void has() {
        assertFalse(repository.has(new NpcExchange(404, 0, 0, null, 0, null)));
        assertTrue(repository.has(new NpcExchange(2, 0, 0, null, 0, null)));
    }

    @Test
    void all() {
        assertCount(3, repository.all());
    }

    @Test
    void byNpcTemplate() {
        assertCount(0, repository.byNpcTemplate(new NpcTemplate(777, 0, 0, 0, null, null, null, 0, 0, null)));
        assertCount(1, repository.byNpcTemplate(new NpcTemplate(848, 0, 0, 0, null, null, null, 0, 0, null)));
        assertCount(2, repository.byNpcTemplate(new NpcTemplate(878, 0, 0, 0, null, null, null, 0, 0, null)));
    }
}
