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

package fr.quatrevieux.araknemu.game.exploration.interaction.exchange.npc;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.npc.NpcStoreExchange;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.exploration.npc.store.NpcStore;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.ItemBought;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.ItemSold;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.NpcStoreList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StoreDialogTest extends GameBaseCase {
    private GameNpc npc;
    private NpcStore store;
    private ExplorationPlayer player;
    private StoreDialog dialog;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcWithStore();

        player = explorationPlayer();
        npc = container.get(NpcService.class).get(10001);
        store = (NpcStore) npc.exchangeFactory(ExchangeType.NPC_STORE);
        dialog = new StoreDialog(new NpcStoreExchange(player, npc, store));

        requestStack.clear();
    }

    @Test
    void start() {
        player.interactions().start(dialog);

        assertTrue(player.interactions().busy());
        assertSame(dialog, player.interactions().get(ExchangeInteraction.class));

        requestStack.assertAll(
            new ExchangeCreated(ExchangeType.NPC_STORE, npc),
            new NpcStoreList(store.available())
        );
    }

    @Test
    void stop() {
        player.interactions().start(dialog);

        dialog.stop();

        requestStack.assertLast(ExchangeLeaved.accepted());
        assertFalse(player.interactions().busy());
    }

    @Test
    void leave() {
        player.interactions().start(dialog);

        dialog.leave();

        requestStack.assertLast(ExchangeLeaved.accepted());
        assertFalse(player.interactions().busy());
    }

    @Test
    void buySuccess() {
        dialog.buy(39, 2);

        assertEquals(39, player.inventory().get(1).templateId());
        assertEquals(2, player.inventory().get(1).quantity());
        assertEquals(15025, player.inventory().kamas());

        requestStack.assertLast(ItemBought.success());
    }

    @Test
    void buyFailed() {
        dialog.buy(404, 1);

        requestStack.assertLast(ItemBought.failed());
    }

    @Test
    void sellSuccess() {
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(39), 3);

        dialog.sell(entry.id(), 2);

        assertEquals(15245, player.inventory().kamas());
        assertEquals(1, entry.quantity());

        requestStack.assertLast(ItemSold.success());
    }

    @Test
    void sellFailed() {
        dialog.sell(404, 1);

        requestStack.assertLast(ItemSold.failed());
    }
}
