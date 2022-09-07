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

package fr.quatrevieux.araknemu.game.exploration.exchange.npc;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc.StoreDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.exploration.npc.store.NpcStore;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.NpcStoreList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NpcStoreExchangeTest extends GameBaseCase {
    private NpcStoreExchange exchange;
    private GameNpc npc;
    private ExplorationPlayer player;
    private NpcStore store;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcWithStore();

        player = explorationPlayer();
        npc = container.get(NpcService.class).get(10001);
        store = (NpcStore) npc.exchangeFactory(ExchangeType.NPC_STORE);
        exchange = new NpcStoreExchange(player, npc, store);
    }

    @Test
    void send() {
        exchange.send("test");

        requestStack.assertLast("test");
    }

    @Test
    void initialize() {
        exchange.initialize();

        requestStack.assertLast(new NpcStoreList(store.available()));
    }

    @Test
    void start() {
        requestStack.clear();
        player.interactions().start(exchange.dialog());

        requestStack.assertAll(
            new ExchangeCreated(ExchangeType.NPC_STORE, npc),
            new NpcStoreList(store.available())
        );
    }

    @Test
    void dialog() {
        assertInstanceOf(StoreDialog.class, exchange.dialog());
    }

    @Test
    void stop() {
        player.interactions().start(exchange.dialog());
        exchange.stop();

        assertFalse(player.interactions().busy());
    }

    @Test
    void buySuccess() {
        exchange.buy(39, 2);

        assertEquals(15025, player.inventory().kamas());
        assertEquals(39, player.inventory().get(1).templateId());
        assertEquals(2, player.inventory().get(1).quantity());
    }

    @Test
    void buyInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> exchange.buy(39, -5));

        assertEquals(15225, player.inventory().kamas());
        assertEquals(0, player.inventory().stream().count());
    }

    @Test
    void buyInvalidItem() {
        assertThrows(IllegalArgumentException.class, () -> exchange.buy(404, 1));

        assertEquals(15225, player.inventory().kamas());
        assertEquals(0, player.inventory().stream().count());
    }

    @Test
    void buyNotEnoughKamas() {
        assertThrows(IllegalArgumentException.class, () -> exchange.buy(39, 1000000));

        assertEquals(15225, player.inventory().kamas());
        assertEquals(0, player.inventory().stream().count());
    }

    @Test
    void sellItemNotFound() {
        assertThrows(InventoryException.class, () -> exchange.sell(404, 5));
        assertEquals(15225, player.inventory().kamas());
    }

    @Test
    void sellTooMany() {
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(39), 3);

        assertThrows(InventoryException.class, () -> exchange.sell(entry.id(), 5));
        assertEquals(15225, player.inventory().kamas());
        assertEquals(3, entry.quantity());
    }

    @Test
    void sellSuccess() {
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(39), 3);

        exchange.sell(entry.id(), 2);
        assertEquals(15245, player.inventory().kamas());
        assertEquals(1, entry.quantity());
    }

    @Test
    void sellSuccessWithoutKamas() {
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(8213), 100);

        exchange.sell(entry.id(), 100);
        assertEquals(15225, player.inventory().kamas());
        assertEquals(0, entry.quantity());
    }
}
