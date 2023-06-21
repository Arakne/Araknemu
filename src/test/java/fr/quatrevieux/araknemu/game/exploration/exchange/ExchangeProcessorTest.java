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

package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.player.PlayerExchangePartyProcessor;
import fr.quatrevieux.araknemu.game.exploration.exchange.player.PlayerExchangeStorage;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExchangeProcessorTest extends GameBaseCase {
    private ExplorationPlayer player1;
    private ExplorationPlayer player2;
    private PlayerExchangeStorage storage1;
    private PlayerExchangeStorage storage2;
    private ExchangeProcessor processor;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        player1 = explorationPlayer();
        player2 = makeOtherExplorationPlayer();

        ExchangeInteraction interaction1 = Mockito.mock(ExchangeInteraction.class);
        ExchangeInteraction interaction2 = Mockito.mock(ExchangeInteraction.class);
        Mockito.when(interaction1.start()).thenReturn(interaction1);
        Mockito.when(interaction2.start()).thenReturn(interaction2);

        player1.interactions().start(interaction1);
        player2.interactions().start(interaction2);

        processor = new ExchangeProcessor(
            new PlayerExchangePartyProcessor(player1, storage1 = new PlayerExchangeStorage(player1)),
            new PlayerExchangePartyProcessor(player2, storage2 = new PlayerExchangeStorage(player2))
        );
    }

    @Test
    void cancel() {
        processor.cancel();

        assertFalse(player1.interactions().busy());
        assertFalse(player2.interactions().busy());
    }

    @Test
    void accepted() {
        assertFalse(processor.accepted());

        storage1.setAccepted(true);
        assertFalse(processor.accepted());

        storage2.setAccepted(true);
        assertTrue(processor.accepted());
    }

    @Test
    void resetAccept() {
        storage1.setAccepted(true);
        storage2.setAccepted(true);

        processor.resetAccept();

        assertFalse(storage1.accepted());
        assertFalse(storage2.accepted());
    }

    @Test
    void assertNotAccepted() {
        processor.assertNotAccepted();

        storage1.setAccepted(true);
        processor.assertNotAccepted();

        storage2.setAccepted(true);
        assertThrows(IllegalStateException.class, () -> processor.assertNotAccepted());
    }

    @Test
    void processSuccess() {
        player2.inventory().addKamas(1000);
        ItemEntry entry1 = player1.inventory().add(container.get(ItemService.class).create(2422));
        ItemEntry entry2 = player2.inventory().add(container.get(ItemService.class).create(39), 5);

        storage1.setItem(entry1, 1);
        storage2.setItem(entry2, 3);
        storage2.setKamas(700);

        processor.process();

        assertFalse(player1.interactions().busy());
        assertFalse(player2.interactions().busy());

        assertEquals(0, entry1.quantity());
        assertEquals(2, entry2.quantity());
        assertEquals(300, player2.inventory().kamas());
        assertEquals(15925, player1.inventory().kamas());
    }

    @Test
    void processFail() {
        storage1.setKamas(100000);
        storage2.setKamas(100000);

        storage1.setAccepted(true);
        storage2.setAccepted(true);

        processor.process();

        assertTrue(player1.interactions().busy());
        assertTrue(player2.interactions().busy());

        assertFalse(storage1.accepted());
        assertFalse(storage2.accepted());

        assertEquals(15225, storage1.kamas());
        assertEquals(0, storage2.kamas());

        assertEquals(15225, player1.inventory().kamas());
        assertEquals(0, player2.inventory().kamas());
    }
}
