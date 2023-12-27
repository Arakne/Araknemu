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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.player.inventory.slot.constraint;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.SimpleItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.exception.AlreadyEquippedException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlots;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EquipOnceConstraintTest extends GameBaseCase {
    private EquipOnceConstraint constraint;
    private InventorySlots inventorySlots;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushHighLevelItems()
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
        constraint = new EquipOnceConstraint(inventorySlots, new int[] {0, 1}, false);
    }

    @Test
    void checkError() throws ContainerException, InventoryException {
        Item item = container.get(ItemService.class).create(2425);
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 694, new ArrayList<>(), 1, -1), item);
        inventorySlots.get(0).uncheckedSet(entry);

        assertThrows(AlreadyEquippedException.class, () -> constraint.check(item, 1));
    }

    @Test
    void checkErrorItemWithoutItemSet() throws ContainerException, InventoryException {
        Item item = container.get(ItemService.class).create(40);
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 40, new ArrayList<>(), 1, -1), item);
        inventorySlots.get(0).uncheckedSet(entry);

        assertThrows(AlreadyEquippedException.class, () -> constraint.check(item, 1));
    }

    @Test
    void checkErrorOnlyWithinItemSet() throws ContainerException, InventoryException {
        constraint = new EquipOnceConstraint(inventorySlots, new int[] {0, 1}, true);

        Item item = container.get(ItemService.class).create(2425);
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 694, new ArrayList<>(), 1, -1), item);
        inventorySlots.get(0).uncheckedSet(entry);

        assertThrows(AlreadyEquippedException.class, () -> constraint.check(item, 1));
    }

    @Test
    void checkOnlyWithinItemSetIgnoreItemWithoutItemSet() throws ContainerException, InventoryException {
        constraint = new EquipOnceConstraint(inventorySlots, new int[] {0, 1}, true);

        Item item = container.get(ItemService.class).create(40);
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 40, new ArrayList<>(), 1, -1), item);
        inventorySlots.get(0).uncheckedSet(entry);

        constraint.check(item, 1); // no exception
    }

    @Test
    void checkSuccessIfNotSameItem() throws ContainerException, InventoryException {
        Item item = container.get(ItemService.class).create(2425);
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 694, new ArrayList<>(), 1, -1), item);
        inventorySlots.get(0).uncheckedSet(entry);

        constraint.check(container.get(ItemService.class).create(39), 1);
    }

    @Test
    void checkSuccess() throws ContainerException, InventoryException {
        constraint.check(container.get(ItemService.class).create(2425), 1);
    }
}
