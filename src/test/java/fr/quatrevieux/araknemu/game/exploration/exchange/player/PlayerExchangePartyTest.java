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
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.player.PlayerExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeAccepted;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.distant.DistantExchangeKamas;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.distant.DistantExchangeObject;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.local.LocalExchangeKamas;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.local.LocalExchangeObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerExchangePartyTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    private PlayerExchangeParty local;
    private PlayerExchangeParty distant;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        PlayerExchangeParty[] parties = PlayerExchangeParty.make(player, other);

        local = parties[0];
        distant = parties[1];
    }

    @Test
    void getters() {
        Assertions.assertEquals(ExchangeType.PLAYER_EXCHANGE, local.type());
        assertEquals(ExchangeType.PLAYER_EXCHANGE, distant.type());

        assertEquals(player, local.actor());
        assertEquals(other, distant.actor());
    }

    @Test
    void dialog() {
        assertInstanceOf(ExchangeDialog.class, local.dialog());
    }

    @Test
    void start() {
        local.start();

        assertNotNull(player.interactions().get(ExchangeDialog.class));
        requestStack.assertLast(new ExchangeCreated(ExchangeType.PLAYER_EXCHANGE));
    }

    @Test
    void leave() {
        local.start();
        distant.start();

        requestStack.clear();

        local.leave();

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());

        requestStack.assertLast(ExchangeLeaved.cancelled());
    }

    @Test
    void send() {
        local.send("my packet");

        requestStack.assertLast("my packet");
    }

    @Test
    void kamasNegative() {
        assertThrows(IllegalArgumentException.class, () -> local.kamas(-5));
    }

    @Test
    void kamasAlreadyAccepted() {
        local.start();
        distant.start();

        local.toggleAccept();
        distant.toggleAccept();

        assertThrows(IllegalStateException.class, () -> local.kamas(5));
    }

    @Test
    void kamasShouldChangeTheCurrentKamasAndSendPacket() {
        local.kamas(10);
        requestStack.assertLast(new LocalExchangeKamas(10));

        local.kamas(100);
        requestStack.assertLast(new LocalExchangeKamas(100));
    }

    @Test
    void kamasHigherThatCurrentKamasShouldLimit() {
        local.kamas(1000000);
        requestStack.assertLast(new LocalExchangeKamas(15225));
    }

    @Test
    void kamasDistantShouldSendPacketToLocal() {
        other.inventory().addKamas(1000);

        distant.kamas(1000);
        requestStack.assertLast(new DistantExchangeKamas(1000));
    }

    @Test
    void kamasShouldUnsetCurrentAcceptedState() {
        local.toggleAccept();
        requestStack.clear();

        local.kamas(1);
        requestStack.assertOne(new ExchangeAccepted(false, player));
    }

    @Test
    void kamasShouldUnsetDistantAcceptedState() {
        distant.toggleAccept();
        requestStack.clear();

        local.kamas(1);
        requestStack.assertOne(new ExchangeAccepted(false, other));
    }

    @Test
    void itemAlreadyAcceptedShouldRaiseException() {
        local.start();
        distant.start();

        local.toggleAccept();
        distant.toggleAccept();

        InventoryEntry entry = player.inventory().add(
            container.get(ItemService.class).create(2422)
        );

        assertThrows(IllegalStateException.class, () -> local.item(entry.id(), 1));
    }

    @Test
    void itemShouldModifyQuantity() {
        InventoryEntry entry = player.inventory().add(
            container.get(ItemService.class).create(2422),
            10
        );

        local.item(entry.id(), 2);
        requestStack.assertLast(new LocalExchangeObject(entry, 2));

        local.item(entry.id(), 1);
        requestStack.assertLast(new LocalExchangeObject(entry, 3));

        local.item(entry.id(), -2);
        requestStack.assertLast(new LocalExchangeObject(entry, 1));
    }

    @Test
    void itemAddAndRemove() {
        InventoryEntry entry = player.inventory().add(
            container.get(ItemService.class).create(2422),
            10
        );

        local.item(entry.id(), 2);
        requestStack.assertLast(new LocalExchangeObject(entry, 2));

        local.item(entry.id(), -2);
        requestStack.assertLast(new LocalExchangeObject(entry, 0));
    }

    @Test
    void itemShouldLimitQuantity() {
        InventoryEntry entry = player.inventory().add(
            container.get(ItemService.class).create(2422),
            2
        );

        local.item(entry.id(), 3);
        requestStack.assertLast(new LocalExchangeObject(entry, 2));

        local.item(entry.id(), -10);
        requestStack.assertLast(new LocalExchangeObject(entry, 0));
    }

    @Test
    void itemShouldUnsetCurrentAcceptedState() {
        InventoryEntry entry = player.inventory().add(
            container.get(ItemService.class).create(2422),
            10
        );

        local.toggleAccept();
        requestStack.clear();

        local.item(entry.id(), 1);
        requestStack.assertOne(new ExchangeAccepted(false, player));
    }

    @Test
    void itemShouldUnsetDistantAcceptedState() {
        InventoryEntry entry = player.inventory().add(
            container.get(ItemService.class).create(2422),
            10
        );

        distant.toggleAccept();
        requestStack.clear();

        local.item(entry.id(), 1);
        requestStack.assertOne(new ExchangeAccepted(false, other));
    }

    @Test
    void toggleAccept() {
        local.toggleAccept();
        requestStack.assertOne(new ExchangeAccepted(true, player));

        local.toggleAccept();
        requestStack.assertOne(new ExchangeAccepted(false, player));
    }

    @Test
    void toggleAcceptDistantShouldSendPacketToLocal() {
        distant.toggleAccept();
        requestStack.assertOne(new ExchangeAccepted(true, other));

        distant.toggleAccept();
        requestStack.assertOne(new ExchangeAccepted(false, other));
    }

    @Test
    void processShouldLeaveExchange() {
        local.start();
        distant.start();

        local.toggleAccept();
        distant.toggleAccept();

        requestStack.assertLast(new ExchangeLeaved(true));

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());
    }

    @Test
    void processWithItemShouldRemoveTheExchangedQuantity() {
        local.start();
        distant.start();

        InventoryEntry entry = player.inventory().add(
            container.get(ItemService.class).create(2422),
            10
        );

        local.item(entry.id(), 3);

        local.toggleAccept();
        distant.toggleAccept();

        assertEquals(7, entry.quantity());
        assertEquals(3, other.inventory().stream()
            .filter(newEntry -> newEntry.item().equals(entry.item()))
            .findFirst().get().quantity()
        );
    }

    @Test
    void processWithItemAllQuantityShouldRemoveTheEntryFromInventory() {
        local.start();
        distant.start();

        InventoryEntry entry = player.inventory().add(container.get(ItemService.class).create(2422));

        local.item(entry.id(), 1);

        local.toggleAccept();
        distant.toggleAccept();

        assertEquals(0, entry.quantity());
        assertFalse(player.inventory().stream().anyMatch(entry::equals));
        assertEquals(1, other.inventory().stream()
            .filter(newEntry -> newEntry.item().equals(entry.item()))
            .findFirst().get().quantity()
        );
    }

    @Test
    void processWithItemShouldStack() {
        local.start();
        distant.start();

        Item item = container.get(ItemService.class).create(2422);
        InventoryEntry localEntry = player.inventory().add(item, 10);
        InventoryEntry distantEntry = other.inventory().add(item, 10);

        local.item(localEntry.id(), 3);

        local.toggleAccept();
        distant.toggleAccept();

        assertEquals(7, localEntry.quantity());
        assertEquals(13, distantEntry.quantity());
    }

    @Test
    void processWithKamas() {
        other.inventory().addKamas(1000);

        local.start();
        distant.start();

        local.kamas(5000);
        distant.kamas(500);

        local.toggleAccept();
        distant.toggleAccept();

        assertEquals(10725, player.inventory().kamas());
        assertEquals(5500, other.inventory().kamas());
    }

    @Test
    void processComplex() {
        InventoryEntry localEntry1 = player.inventory().add(container.get(ItemService.class).create(39));
        InventoryEntry localEntry2 = player.inventory().add(container.get(ItemService.class).create(40));
        InventoryEntry distantEntry = other.inventory().add(container.get(ItemService.class).create(2422));

        local.start();
        distant.start();

        local.kamas(5000);
        local.item(localEntry1.id(), 1);
        local.item(localEntry2.id(), 1);

        distant.item(distantEntry.id(), 1);

        local.toggleAccept();
        distant.toggleAccept();

        assertEquals(0, localEntry1.quantity());
        assertEquals(0, localEntry2.quantity());
        assertEquals(0, distantEntry.quantity());

        assertEquals(1, player.inventory().stream().filter(newEntry -> newEntry.item().equals(distantEntry.item())).findFirst().get().quantity());
        assertEquals(1, other.inventory().stream().filter(newEntry -> newEntry.item().equals(localEntry1.item())).findFirst().get().quantity());
        assertEquals(1, other.inventory().stream().filter(newEntry -> newEntry.item().equals(localEntry2.item())).findFirst().get().quantity());

        assertEquals(10225, player.inventory().kamas());
        assertEquals(5000, other.inventory().kamas());
    }

    @Test
    void processWithLocalInvalidKamasShouldResetExchange() {
        local.start();
        distant.start();

        local.kamas(5000);

        player.inventory().removeKamas(15000);
        requestStack.clear();

        local.toggleAccept();
        distant.toggleAccept();

        assertTrue(player.interactions().busy());
        assertTrue(other.interactions().busy());

        assertEquals(225, player.inventory().kamas());
        assertEquals(0, other.inventory().kamas());

        requestStack.assertOne(new ExchangeAccepted(false, player));
        requestStack.assertOne(new ExchangeAccepted(false, other));
        requestStack.assertOne(new LocalExchangeKamas(225));
    }

    @Test
    void processWithDistantInvalidKamasShouldResetExchange() {
        other.inventory().addKamas(5000);

        local.start();
        distant.start();

        distant.kamas(1000);

        other.inventory().removeKamas(4500);
        requestStack.clear();

        local.toggleAccept();
        distant.toggleAccept();

        assertTrue(player.interactions().busy());
        assertTrue(other.interactions().busy());

        assertEquals(15225, player.inventory().kamas());
        assertEquals(500, other.inventory().kamas());

        requestStack.assertOne(new ExchangeAccepted(false, player));
        requestStack.assertOne(new ExchangeAccepted(false, other));
        requestStack.assertOne(new DistantExchangeKamas(500));
    }

    @Test
    void processWithLocalInvalidQuantityShouldResetExchange() {
        local.start();
        distant.start();

        InventoryEntry entry = player.inventory().add(
            container.get(ItemService.class).create(2422),
            10
        );

        local.item(entry.id(), 3);
        entry.remove(8);
        requestStack.clear();

        local.toggleAccept();
        distant.toggleAccept();

        assertTrue(player.interactions().busy());
        assertTrue(other.interactions().busy());

        assertEquals(2, entry.quantity());
        assertFalse(other.inventory().stream().anyMatch(newEntry -> newEntry.item().equals(entry.item())));

        requestStack.assertOne(new ExchangeAccepted(false, player));
        requestStack.assertOne(new ExchangeAccepted(false, other));
        requestStack.assertOne(new LocalExchangeObject(entry, 2));
    }

    @Test
    void processWithDistantInvalidQuantityShouldResetExchange() {
        local.start();
        distant.start();

        InventoryEntry entry = other.inventory().add(
            container.get(ItemService.class).create(2422),
            10
        );

        distant.item(entry.id(), 3);
        entry.remove(8);
        requestStack.clear();

        local.toggleAccept();
        distant.toggleAccept();

        assertTrue(player.interactions().busy());
        assertTrue(other.interactions().busy());

        assertEquals(2, entry.quantity());
        assertFalse(player.inventory().stream().anyMatch(newEntry -> newEntry.item().equals(entry.item())));

        requestStack.assertOne(new ExchangeAccepted(false, player));
        requestStack.assertOne(new ExchangeAccepted(false, other));
        requestStack.assertOne(new DistantExchangeObject(entry, 2));
    }

    @Test
    void processWithLocalAndDistantInvalidKamasShouldResetExchangeForBoth() {
        other.inventory().addKamas(5000);

        local.start();
        distant.start();

        local.kamas(5000);
        distant.kamas(5000);

        player.inventory().removeKamas(15000);
        other.inventory().removeKamas(4000);
        requestStack.clear();

        local.toggleAccept();
        distant.toggleAccept();

        requestStack.assertOne(new LocalExchangeKamas(225));
        requestStack.assertOne(new DistantExchangeKamas(1000));
    }
}
