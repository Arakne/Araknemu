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

package fr.quatrevieux.araknemu.game.exploration.exchange.player;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangePartyProcessor;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerExchangePartyProcessorTest extends GameBaseCase {
    private ExplorationPlayer player;
    private PlayerExchangeStorage storage;
    private PlayerExchangePartyProcessor processor;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        player = explorationPlayer();
        processor = new PlayerExchangePartyProcessor(player, storage = new PlayerExchangeStorage(player));
    }

    @Test
    void validateEmpty() {
        assertTrue(processor.validate());
    }

    @Test
    void validateSuccess() {
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(2422));

        storage.setItem(entry, 1);
        storage.setKamas(100);

        assertTrue(processor.validate());
    }

    @Test
    void validateFailed() {
        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(2422));

        storage.setItem(entry, 5);
        storage.setKamas(100000);

        assertFalse(processor.validate());
        assertEquals(15225, storage.kamas());
        assertEquals(1, storage.quantity(entry));
    }

    @Test
    void terminateAccepted() {
        ExchangeInteraction interaction = Mockito.mock(ExchangeInteraction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);
        player.interactions().start(interaction);

        processor.terminate(true);

        assertFalse(player.interactions().busy());
        requestStack.assertLast(new ExchangeLeaved(true));
    }

    @Test
    void terminateNotAccepted() {
        ExchangeInteraction interaction = Mockito.mock(ExchangeInteraction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);
        player.interactions().start(interaction);

        processor.terminate(false);

        assertFalse(player.interactions().busy());
        requestStack.assertLast(new ExchangeLeaved(false));
    }

    @Test
    void accepted() {
        assertFalse(processor.accepted());

        storage.setAccepted(true);
        assertTrue(processor.accepted());

        processor.resetAccept();
        assertFalse(processor.accepted());
    }

    @Test
    void addKamas() {
        processor.addKamas(100);

        assertEquals(15325, player.inventory().kamas());
    }

    @Test
    void addItem() {
        Item item = container.get(ItemService.class).create(2422);

        processor.addItem(item, 2);

        InventoryEntry entry = player.inventory().get(1);

        assertEquals(2, entry.quantity());
        assertEquals(item, entry.item());
    }

    @Test
    void process() {
        ExchangePartyProcessor otherProcess = Mockito.mock(ExchangePartyProcessor.class);

        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(2422), 3);
        ItemEntry other = player.inventory().add(container.get(ItemService.class).create(39));

        storage.setKamas(100);
        storage.setItem(entry, 2);
        storage.setItem(other, 0);

        processor.process(otherProcess);

        Mockito.verify(otherProcess).addKamas(100);
        Mockito.verify(otherProcess).addItem(entry.item(), 2);
        Mockito.verify(otherProcess, Mockito.never()).addItem(other.item(), 0);

        assertEquals(1, entry.quantity());
        assertEquals(1, other.quantity());
        assertEquals(15125, player.inventory().kamas());
    }

    @Test
    void processEmpty() {
        ExchangePartyProcessor otherProcess = Mockito.mock(ExchangePartyProcessor.class);
        processor.process(otherProcess);

        Mockito.verify(otherProcess, Mockito.never()).addItem(Mockito.any(Item.class), Mockito.anyInt());
        Mockito.verify(otherProcess, Mockito.never()).addKamas(Mockito.anyInt());
    }
}
