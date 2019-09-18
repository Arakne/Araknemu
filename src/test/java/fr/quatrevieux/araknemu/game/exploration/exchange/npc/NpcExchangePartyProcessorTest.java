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
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangePartyProcessor;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.AcceptChanged;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.ItemMoved;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.KamasChanged;
import fr.quatrevieux.araknemu.game.exploration.exchange.player.PlayerExchangeStorage;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.exploration.npc.exchange.GameNpcExchange;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class NpcExchangePartyProcessorTest extends GameBaseCase {
    private NpcExchangePartyProcessor processor;
    private GameNpc npc;

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
        dataSet.pushNpcExchange(3, 878, 0, "39:2", 0, "2425");
        dataSet.pushNpcExchange(4, 878, 10, "2411", 1000, "2414;2425:2");

        npc = container.get(NpcService.class).get(472);

        processor = new NpcExchangePartyProcessor(npc, (GameNpcExchange) npc.exchangeFactory(ExchangeType.NPC_EXCHANGE));
    }

    @Test
    void noop() {
        processor.terminate(true);
        processor.resetAccept();
        processor.addKamas(1);
        processor.addItem(null, 0);
    }

    @Test
    void accepted() throws SQLException {
        ExplorationPlayer player = explorationPlayer();
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(2411));
        PlayerExchangeStorage storage = new PlayerExchangeStorage(player);
        storage.dispatcher().register(processor);

        AtomicReference<AcceptChanged> ref = new AtomicReference<>();
        processor.dispatcher().add(AcceptChanged.class, ref::set);

        assertFalse(processor.accepted());

        storage.setItem(entry, 1);
        assertTrue(processor.accepted());
        assertTrue(ref.get().accepted());

        storage.setKamas(1);
        assertFalse(processor.accepted());
        assertFalse(ref.get().accepted());

        ref.set(null);
        storage.setKamas(2);
        assertNull(ref.get());

        storage.setKamas(0);
        assertTrue(processor.accepted());
        assertTrue(ref.get().accepted());
    }

    @Test
    void itemEvents() throws SQLException {
        ExplorationPlayer player = explorationPlayer();
        PlayerExchangeStorage storage = new PlayerExchangeStorage(player);
        storage.dispatcher().register(processor);

        AtomicReference<ItemMoved> item = new AtomicReference<>();
        processor.dispatcher().add(ItemMoved.class, item::set);

        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(39));
        storage.setItem(entry, 2);

        assertInstanceOf(NpcExchangeItemEntry.class, item.get().entry());
        assertEquals(2425, item.get().entry().id());
        assertEquals(1, item.get().quantity());

        storage.setItem(entry, 1);
        assertEquals(2425, item.get().entry().id());
        assertEquals(0, item.get().quantity());

        item.set(null);

        storage.setItem(entry, 3);
        assertNull(item.get());
    }

    @Test
    void kamasEvents() throws SQLException {
        ExplorationPlayer player = explorationPlayer();
        PlayerExchangeStorage storage = new PlayerExchangeStorage(player);
        storage.dispatcher().register(processor);

        AtomicReference<KamasChanged> ref = new AtomicReference<>();
        processor.dispatcher().add(KamasChanged.class, ref::set);

        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(2411));
        storage.setItem(entry, 1);

        assertEquals(1000, ref.get().quantity());

        ref.set(null);
        storage.setKamas(10);
        assertNull(ref.get());

        storage.setKamas(1);
        assertEquals(0, ref.get().quantity());
    }

    @Test
    void process() throws SQLException {
        ExplorationPlayer player = explorationPlayer();
        PlayerExchangeStorage storage = new PlayerExchangeStorage(player);
        storage.dispatcher().register(processor);

        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(2411));
        storage.setItem(entry, 1);

        ExchangePartyProcessor otherProcessor = Mockito.mock(ExchangePartyProcessor.class);

        processor.process(otherProcessor);

        Mockito.verify(otherProcessor).addKamas(1000);
        Mockito.verify(otherProcessor).addItem(Mockito.argThat(argument -> argument.template().id() == 2414), Mockito.eq(1));
        Mockito.verify(otherProcessor, Mockito.times(2)).addItem(Mockito.argThat(argument -> argument.template().id() == 2425), Mockito.eq(1));
    }

    @Test
    void processWithoutKamas() throws SQLException {
        ExplorationPlayer player = explorationPlayer();
        PlayerExchangeStorage storage = new PlayerExchangeStorage(player);
        storage.dispatcher().register(processor);

        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(39), 2);
        storage.setItem(entry, 2);

        ExchangePartyProcessor otherProcessor = Mockito.mock(ExchangePartyProcessor.class);

        processor.process(otherProcessor);

        Mockito.verify(otherProcessor, Mockito.never()).addKamas(Mockito.anyInt());
        Mockito.verify(otherProcessor).addItem(Mockito.argThat(argument -> argument.template().id() == 2425), Mockito.eq(1));
    }
}
