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

package fr.quatrevieux.araknemu.game.handler.exchange.store;

import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeFactory;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.network.game.in.exchange.store.BuyRequest;
import fr.quatrevieux.araknemu.network.game.out.exchange.store.ItemBought;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuyItemTest extends GameBaseCase {
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcWithStore();

        player = explorationPlayer();
    }

    @Test
    void success() throws Exception {
        openStore();

        handlePacket(new BuyRequest(39, 5));

        requestStack.assertLast(ItemBought.success());
        assertEquals(14725, player.inventory().kamas());
        assertEquals(39, player.inventory().get(1).templateId());
        assertEquals(5, player.inventory().get(1).quantity());
    }

    @Test
    void failedItemNotAvailable() throws Exception {
        openStore();

        handlePacket(new BuyRequest(404, 5));

        requestStack.assertLast(ItemBought.failed());
    }

    @Test
    void failedNotInExploration() {
        session.setExploration(null);
        assertThrows(CloseImmediately.class, () -> handlePacket(new BuyRequest(39, 5)));
    }

    private void openStore() {
        player.interactions().start(container.get(ExchangeFactory.class).create(
            ExchangeType.NPC_STORE,
            player,
            container.get(NpcService.class).get(10001)
        ));
    }
}
