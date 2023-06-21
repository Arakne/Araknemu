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

package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectMoved;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryEntryTest extends GameBaseCase {
    private PlayerInventory inventory;
    private ListenerAggregate dispatcher;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();

        inventory = new PlayerInventory(
            gamePlayer(true),
            dataSet.refresh(new Player(gamePlayer().id())),
            Collections.emptyList()
        );

        dispatcher = gamePlayer().dispatcher();
    }

    @Test
    void getters() throws InventoryException, ContainerException {
        Item item = container.get(ItemService.class).create(284);

        InventoryEntry entry = inventory.add(item, 12, -1);

        assertEquals(1, entry.id());
        assertEquals(item, entry.item());
        assertEquals(12, entry.quantity());
        assertEquals(-1, entry.position());
        assertEquals(new ArrayList<>(), entry.effects());
        assertEquals(284, entry.templateId());
    }

    @Test
    void moveNegativeQuantity() throws InventoryException, ContainerException {
        InventoryEntry entry = inventory.add(container.get(ItemService.class).create(284));

        assertThrows(InventoryException.class, () -> entry.move(0, -5), "Invalid quantity given");
    }

    @Test
    void moveTooHighQuantity() throws InventoryException, ContainerException {
        InventoryEntry entry = inventory.add(container.get(ItemService.class).create(284));

        assertThrows(InventoryException.class, () -> entry.move(0, 100), "Invalid quantity given");
    }

    @Test
    void moveAlreadyOnRequestedPosition() throws InventoryException, ContainerException {
        InventoryEntry entry = inventory.add(container.get(ItemService.class).create(284));

        assertThrowsWithMessage(InventoryException.class, "The item is already on the requested position", () -> entry.move(-1, 1));
    }

    @Test
    void moveAll() throws InventoryException, ContainerException {
        InventoryEntry entry = inventory.add(
            container.get(ItemService.class).create(40)
        );

        AtomicReference<ItemEntry> ref = new AtomicReference<>();
        dispatcher.add(ObjectMoved.class, objectMoved -> ref.set(objectMoved.entry()));

        entry.move(1, 1);

        assertSame(ref.get(), entry);
        assertEquals(1, entry.position());
    }

    @Test
    void movePartial() throws InventoryException, ContainerException {
        Item item = container.get(ItemService.class).create(40);
        InventoryEntry entry = inventory.add(item, 10);

        AtomicReference<ItemEntry> ref1 = new AtomicReference<>();
        AtomicReference<ItemEntry> ref2 = new AtomicReference<>();
        dispatcher.add(new Listener<ObjectAdded>() {
            @Override
            public void on(ObjectAdded event) {
                ref1.set(event.entry());
            }

            @Override
            public Class<ObjectAdded> event() {
                return ObjectAdded.class;
            }
        });
        dispatcher.add(new Listener<ObjectQuantityChanged>() {
            @Override
            public void on(ObjectQuantityChanged event) {
                ref2.set(event.entry());
            }

            @Override
            public Class<ObjectQuantityChanged> event() {
                return ObjectQuantityChanged.class;
            }
        });

        entry.move(1, 1);

        assertEquals(-1, entry.position());
        assertEquals(9, entry.quantity());
        assertSame(entry, ref2.get());

        assertInstanceOf(InventoryEntry.class, ref1.get());
        assertEquals(1, ref1.get().position());
        assertEquals(1, ref1.get().quantity());
        assertSame(item, ref1.get().item());
    }

    @Test
    void add() throws InventoryException, ContainerException {
        InventoryEntry entry = inventory.add(
            container.get(ItemService.class).create(284),
            12
        );

        AtomicReference<ObjectQuantityChanged> ref = new AtomicReference<>();
        dispatcher.add(ObjectQuantityChanged.class, ref::set);

        entry.add(3);

        assertEquals(15, entry.quantity());
        assertSame(entry, ref.get().entry());
    }

    @Test
    void remove() throws InventoryException, ContainerException {
        InventoryEntry entry = inventory.add(
            container.get(ItemService.class).create(284),
            12
        );

        AtomicReference<ObjectQuantityChanged> ref = new AtomicReference<>();
        dispatcher.add(ObjectQuantityChanged.class, ref::set);

        entry.remove(3);

        assertEquals(9, entry.quantity());
        assertSame(entry, ref.get().entry());
    }

    @Test
    void removeTooHighQuantity() throws InventoryException, ContainerException {
        InventoryEntry entry = inventory.add(
            container.get(ItemService.class).create(284),
            12
        );

        AtomicReference<ObjectQuantityChanged> ref = new AtomicReference<>();
        dispatcher.add(ObjectQuantityChanged.class, ref::set);

        assertThrows(InventoryException.class, () -> entry.remove(25));

        assertEquals(12, entry.quantity());
        assertNull(ref.get());
    }

    @Test
    void removeAll() throws InventoryException, ContainerException {
        InventoryEntry entry = inventory.add(
            container.get(ItemService.class).create(284),
            12
        );

        AtomicReference<ObjectDeleted> ref = new AtomicReference<>();
        dispatcher.add(ObjectDeleted.class, ref::set);

        entry.remove(12);

        assertEquals(0, entry.quantity());
        assertSame(entry, ref.get().entry());

        assertThrows(ItemNotFoundException.class, () -> inventory.get(entry.id()));
    }
}
