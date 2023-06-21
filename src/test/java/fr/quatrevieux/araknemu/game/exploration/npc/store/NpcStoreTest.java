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

package fr.quatrevieux.araknemu.game.exploration.npc.store;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.npc.NpcStoreExchange;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NpcStoreTest extends GameBaseCase {
    private NpcStore store;
    private ItemService itemService;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        ItemTemplateRepository repository = container.get(ItemTemplateRepository.class);

        store = new NpcStore(
            itemService = container.get(ItemService.class),
            configuration.economy(),
            Arrays.asList(repository.get(39), repository.get(2425))
        );
    }

    @Test
    void available() {
        assertArrayEquals(
            store.available().stream().map(ItemTemplate::id).toArray(),
            new Object[] {39, 2425}
        );
    }

    @Test
    void has() {
        assertTrue(store.has(39));
        assertTrue(store.has(2425));
        assertFalse(store.has(404));
    }

    @Test
    void buyOneWithFixedStats() {
        Map<Item, Integer> ret = store.buy(39, 1).items();

        assertEquals(1, ret.size());
        assertTrue(ret.containsKey(itemService.create(39)));
        assertEquals(1, (int) ret.get(itemService.create(39)));
    }

    @Test
    void buyMultipleWithFixedStats() {
        Map<Item, Integer> ret = store.buy(39, 5).items();

        assertEquals(1, ret.size());
        assertTrue(ret.containsKey(itemService.create(39)));
        assertEquals(5, (int) ret.get(itemService.create(39)));
    }

    @Test
    void buyOneWithRandomStats() {
        Map<Item, Integer> ret = store.buy(2425, 1).items();

        assertEquals(1, ret.size());

        Item item = new ArrayList<>(ret.keySet()).get(0);

        assertEquals(2425, item.template().id());
        assertEquals(1, (int) ret.get(item));

        Map<Item, Integer> ret2 = store.buy(2425, 1).items();
        Item other = new ArrayList<>(ret2.keySet()).get(0);

        assertEquals(2425, other.template().id());
        assertNotEquals(item, other);
    }

    @Test
    void buyMultipleWithRandomStats() {
        Map<Item, Integer> ret = store.buy(2425, 5).items();

        assertTrue(ret.size() > 1);
        assertTrue(ret.keySet().stream().allMatch(item -> item.template().id() == 2425));
        assertEquals(5, ret.values().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void price() {
        assertEquals(100, store.buy(39, 1).price());
        assertEquals(500, store.buy(39, 5).price());
        assertEquals(2750, store.buy(2425, 5).price());
    }

    @Test
    void sellPrice() {
        Item item = container.get(ItemService.class).create(39);

        assertEquals(10, store.sellPrice(item, 1));
        assertEquals(50, store.sellPrice(item, 5));

        item = container.get(ItemService.class).create(2425);
        assertEquals(275, store.sellPrice(item, 5));

        item = container.get(ItemService.class).create(8213);
        assertEquals(0, store.sellPrice(item, 100));
    }

    @Test
    void factory() throws SQLException {
        dataSet.pushNpcs();

        assertSame(ExchangeType.NPC_STORE, store.type());

        ExplorationPlayer player = explorationPlayer();
        GameNpc npc = container.get(NpcService.class).get(472);

        NpcStoreExchange createdExchange = store.create(player, npc);

        assertSame(npc, createdExchange.seller());
    }
}
