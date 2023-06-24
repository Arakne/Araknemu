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
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.exploration.npc.exchange.GameNpcExchange;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeAccepted;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.distant.DistantExchangeKamas;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.local.LocalExchangeKamas;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.local.LocalExchangeObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NpcExchangePartyTest extends GameBaseCase {
    private GameNpc npc;
    private ExplorationPlayer player;
    private NpcExchangeParty exchangeParty;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushNpcs()
            .pushItemTemplates()
            .pushItemSets()
        ;

        dataSet.pushNpcExchange(1, 878, 100, "39:2", 0, "2422");
        dataSet.pushNpcExchange(2, 878, 0, "2411", 1000, "2414;2425:2");

        npc = container.get(NpcService.class).get(472);
        player = explorationPlayer();
        exchangeParty = new NpcExchangeParty(player, npc, (GameNpcExchange) npc.exchangeFactory(ExchangeType.NPC_EXCHANGE));
    }

    @Test
    void dialog() {
        assertInstanceOf(ExchangeDialog.class, exchangeParty.dialog());

        player.interactions().start(exchangeParty.dialog());
        requestStack.assertLast(new ExchangeCreated(ExchangeType.NPC_EXCHANGE, npc));
    }

    @Test
    void getters() {
        assertEquals(ExchangeType.NPC_EXCHANGE, exchangeParty.type());
        assertSame(player, exchangeParty.actor());
        assertSame(npc, exchangeParty.target());
    }

    @Test
    void leave() {
        player.interactions().start(exchangeParty.dialog());
        exchangeParty.leave();

        requestStack.assertLast(new ExchangeLeaved(false));
        assertFalse(player.interactions().busy());
    }

    @Test
    void acceptWithoutExchangeShouldDoNothing() {
        player.interactions().start(exchangeParty.dialog());
        exchangeParty.toggleAccept();

        requestStack.assertLast(new ExchangeAccepted(true, player));
        assertTrue(player.interactions().busy());
    }

    @Test
    void kamasShouldSendLocalPackets() {
        player.interactions().start(exchangeParty.dialog());
        exchangeParty.kamas(100);

        requestStack.assertLast(new LocalExchangeKamas(100));
    }

    @Test
    void itemShouldSendLocalPackets() {
        player.interactions().start(exchangeParty.dialog());
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(39));

        exchangeParty.item(entry.id(), 1);
        requestStack.assertLast(new LocalExchangeObject(entry, 1));
    }

    @Test
    void distantPackets() {
        player.interactions().start(exchangeParty.dialog());
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(2411));

        exchangeParty.item(entry.id(), 1);

        requestStack.assertOne(new DistantExchangeKamas(1000));
        requestStack.assertOne("EmKO+2425|2|2425|7e#1#a#0#1d10+0,76#1#a#0#1d10+0");
        requestStack.assertOne("EmKO+2414|1|2414|ae#1#12c#0#1d300+0,7d#1#30#0#1d48+0");
        requestStack.assertOne(new ExchangeAccepted(true, npc));

        requestStack.clear();
        exchangeParty.kamas(100);

        requestStack.assertOne(new DistantExchangeKamas(0));
        requestStack.assertOne("EmKO-2425");
        requestStack.assertOne("EmKO-2414");
        requestStack.assertOne(new ExchangeAccepted(false, npc));
        requestStack.clear();

        exchangeParty.item(entry.id(), -1);
        requestStack.assertAll(new LocalExchangeObject(entry, 0));

        ItemEntry otherEntry = player.inventory().add(container.get(ItemService.class).create(39), 5);
        exchangeParty.item(otherEntry.id(), 2);

        requestStack.assertOne("EmKO+2422|1|2422|8a#1#f#0#1d15+0,7d#1#21#0#1d33+0");
        requestStack.assertOne(new ExchangeAccepted(true, npc));
    }

    @Test
    void process() {
        player.interactions().start(exchangeParty.dialog());
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(2411));

        exchangeParty.item(entry.id(), 1);
        exchangeParty.toggleAccept();

        assertFalse(player.interactions().busy());
        requestStack.assertOne(new ExchangeLeaved(true));

        assertEquals(0, entry.quantity());
        assertEquals(16225, player.inventory().kamas());

        ItemEntry playerItem1 = player.inventory().stream().filter(invEntry -> invEntry.templateId() == 2414).findFirst().get();
        assertEquals(1, playerItem1.quantity());

        List<ItemEntry> playerItems2 = player.inventory().stream().filter(invEntry -> invEntry.templateId() == 2425).collect(Collectors.toList());
        assertCount(2, playerItems2);
        assertEquals(1, playerItems2.get(0).quantity());
        assertEquals(1, playerItems2.get(1).quantity());
    }
}
