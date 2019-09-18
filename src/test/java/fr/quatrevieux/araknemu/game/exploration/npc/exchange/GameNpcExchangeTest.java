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

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcExchangeRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.Exchange;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.npc.NpcExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameNpcExchangeTest extends GameBaseCase {
    private GameNpcExchange exchange;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        dataSet.pushNpcExchange(1, 878, 100, "39:2", 10, "2422");
        dataSet.pushNpcExchange(2, 878, 0, "2411", 1000, "2414;2425:2");

        NpcExchangeService service = new NpcExchangeService(
            container.get(ItemService.class),
            container.get(NpcExchangeRepository.class),
            container.get(ItemTemplateRepository.class)
        );

        exchange = service.load(new NpcTemplate(878, 0, 0, 0, null, null, null, 0, 0, null)).get();
    }

    @Test
    void findNotMatching() {
        NpcExchangeEntry entry = exchange.find(new HashMap<>(), 0);

        assertSame(entry, NpcExchangeEntry.NULL_ENTRY);
        assertFalse(entry.valid());
        assertFalse(entry.match(new HashMap<>(), 0));
        assertEquals(0, entry.kamas());
        assertTrue(entry.items().isEmpty());
        assertTrue(entry.generate().isEmpty());
    }

    @Test
    void findMatching() throws SQLException {
        ItemEntry itemEntry = explorationPlayer().inventory().add(container.get(ItemService.class).create(39), 2);

        Map<ItemEntry, Integer> items = new HashMap<>();
        items.put(itemEntry, 2);

        NpcExchangeEntry entry = exchange.find(items, 100);
        assertTrue(entry.valid());
        assertEquals(10, entry.kamas());
        assertCount(1, entry.items());
        assertEquals(2422, new ArrayList<>(entry.items()).get(0).getKey().id());
        assertEquals(1, (int) new ArrayList<>(entry.items()).get(0).getValue());

        Map<Item, Integer> generated = entry.generate();
        assertEquals(1, generated.size());
        assertEquals(2422, new ArrayList<>(generated.keySet()).get(0).template().id());
        assertEquals(1, (int) generated.get(new ArrayList<>(generated.keySet()).get(0)));
    }

    @Test
    void factory() throws SQLException {
        dataSet.pushNpcs();

        assertSame(ExchangeType.NPC_EXCHANGE, exchange.type());

        ExplorationPlayer player = explorationPlayer();
        GameNpc npc = container.get(NpcService.class).get(472);

        NpcExchangeParty createdExchange = exchange.create(player, npc);

        assertSame(player, createdExchange.actor());
        assertSame(npc, createdExchange.target());
    }
}
