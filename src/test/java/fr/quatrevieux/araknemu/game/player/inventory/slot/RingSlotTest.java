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

package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.SimpleItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.exception.BadLevelException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RingSlotTest extends GameBaseCase {
    private RingSlot slot;
    private InventorySlots inventorySlots;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        inventorySlots = new InventorySlots(
            new DefaultListenerAggregate(),
            new SimpleItemStorage<>(
                new DefaultListenerAggregate(),
                null,
                new ArrayList<>()
            ),
            gamePlayer()
        );
        slot = new RingSlot(new DefaultListenerAggregate(), Mockito.mock(ItemStorage.class), gamePlayer(), inventorySlots, RingSlot.RING1);
    }

    @Test
    void checkBadType() throws ContainerException {
        assertThrows(InventoryException.class, () -> slot.check(container.get(ItemService.class).create(2425), 1));
    }

    @Test
    void checkBadQuantity() throws ContainerException {
        assertThrows(InventoryException.class, () -> slot.check(container.get(ItemService.class).create(2419), 10));
    }

    @Test
    void checkAlreadySet() throws ContainerException {
        slot.uncheckedSet(new InventoryEntry(null, null, null));

        assertThrows(InventoryException.class, () -> slot.check(container.get(ItemService.class).create(2419), 1));
    }

    @Test
    void checkAlreadyEquipped() {
        Item item = container.get(ItemService.class).create(2419);

        for (int slotId : new int[] {RingSlot.RING1, RingSlot.RING2}) {
            InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 2419, new ArrayList<>(), 1, -1), item);
            inventorySlots.get(slotId).uncheckedSet(entry);

            assertThrows(InventoryException.class, () -> slot.check(item, 1));
            slot.unset();
        }
    }

    @Test
    void checkAlreadyEquippedNotOnItemSet() {
        Item item = container.get(ItemService.class).create(849);

        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 849, new ArrayList<>(), 1, -1), item);
        inventorySlots.get(RingSlot.RING1).uncheckedSet(entry);

        slot.check(item, 1);
    }

    @Test
    void checkBadLevel() throws ContainerException, SQLException {
        dataSet.pushHighLevelItems();

        assertThrows(BadLevelException.class, () -> slot.check(container.get(ItemService.class).create(112419), 1));
    }

    @Test
    void checkSuccess() throws ContainerException, InventoryException {
        slot.check(container.get(ItemService.class).create(2419), 1);
    }

    @Test
    void setFail() {
        assertThrows(InventoryException.class, () -> slot.set(
            new InventoryEntry(null, new PlayerItem(1, 1, 2425, new ArrayList<>(), 1, -1), container.get(ItemService.class).create(2425)
        )));

        assertFalse(slot.entry().isPresent());
        assertFalse(slot.equipment().isPresent());
    }

    @Test
    void setSuccess() throws ContainerException, InventoryException {
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 2419, new ArrayList<>(), 1, -1), container.get(ItemService.class).create(2419));

        slot.set(entry);

        assertSame(entry, slot.entry().get());
        assertSame(entry.item(), slot.equipment().get());
    }
}
