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

package fr.quatrevieux.araknemu.game.item.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.game.item.type.Wearable;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StackableItemStorageTest extends GameBaseCase {
    private StackableItemStorage<InventoryEntry> storage;
    private ItemStorage<InventoryEntry> baseStorage;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemTemplates();

        GamePlayer player = gamePlayer();

        storage = new StackableItemStorage<>(
            baseStorage = new SimpleItemStorage<>(
                new DefaultListenerAggregate(),
                (id, item, quantity, position) -> new InventoryEntry(
                    player.inventory(),
                    new PlayerItem(1, id, item.template().id(), item.effects().stream().map(ItemEffect::toTemplate).collect(Collectors.toList()), quantity, position),
                    item
                )
            )
        );
    }

    @Test
    void getNotFound() {
        assertThrows(ItemNotFoundException.class, () -> storage.get(5));
    }

    @Test
    void getFound() throws ContainerException, InventoryException {
        InventoryEntry entry = baseStorage.add(
            container.get(ItemService.class).create(39)
        );

        assertSame(entry, storage.get(entry.id()));
    }

    @Test
    void addNotExists() throws ContainerException, InventoryException {
        Item item = container.get(ItemService.class).create(39);
        InventoryEntry entry = storage.add(item);

        assertEquals(39, entry.templateId());
        assertEquals(-1, entry.position());
        assertEquals(1, entry.quantity());
        assertInstanceOf(Wearable.class, entry.item());
        assertSame(entry, storage.get(entry.id()));

        assertSame(entry, storage.find(item).get());
    }

    @Test
    void addExistsWillStack() throws ContainerException, InventoryException {
        Item item = container.get(ItemService.class).create(39);
        InventoryEntry entry = storage.add(item, 5);

        assertSame(entry, storage.add(item, 3));
        assertSame(entry, storage.find(item).get());
        assertEquals(8, entry.quantity());
        assertEquals(-1, entry.position());

        assertIterableEquals(
            Collections.singletonList(entry),
            storage
        );
    }

    @Test
    void addExistsBadPositionWillNoStack() throws ContainerException, InventoryException {
        Item item = container.get(ItemService.class).create(39);
        InventoryEntry entry = storage.add(item, 5);

        InventoryEntry newEntry = storage.add(item, 1, 0);

        assertNotSame(entry, newEntry);
        assertSame(entry, storage.find(item).get());
        assertEquals(5, entry.quantity());
        assertEquals(-1, entry.position());

        assertEquals(1, newEntry.quantity());
        assertEquals(0, newEntry.position());
        assertEquals(item, newEntry.item());

        assertIterableEquals(
            Arrays.asList(entry, newEntry),
            storage
        );
    }

    @Test
    void addExistsInvalidPositionWillChangeStackedItem() throws ContainerException, InventoryException {
        Item item = container.get(ItemService.class).create(39);
        InventoryEntry entry = storage.add(item);
        entry.move(0, 1);

        InventoryEntry newEntry = storage.add(item);

        assertNotSame(entry, newEntry);
        assertEquals(1, entry.quantity());
        assertEquals(0, entry.position());

        assertSame(newEntry, storage.find(item).get());
        assertEquals(1, newEntry.quantity());
        assertEquals(-1, newEntry.position());
        assertEquals(item, newEntry.item());

        assertIterableEquals(
            Arrays.asList(entry, newEntry),
            storage
        );
    }

    @Test
    void addNotExistsAndBadPosition() throws ContainerException, InventoryException {
        Item item = container.get(ItemService.class).create(39);
        InventoryEntry entry = storage.add(item, 1, 5);

        assertEquals(39, entry.templateId());
        assertEquals(5, entry.position());
        assertEquals(1, entry.quantity());
        assertInstanceOf(Wearable.class, entry.item());
        assertSame(entry, storage.get(entry.id()));

        assertFalse(storage.find(item).isPresent());
    }

    @Test
    void createWithNotEmptyStorageWillIndexesItems() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        storage = new StackableItemStorage<>(
            baseStorage = new SimpleItemStorage<>(
                new DefaultListenerAggregate(),
                (id, item, quantity, position) -> new InventoryEntry(
                    player.inventory(),
                    new PlayerItem(1, id, item.template().id(), item.effects().stream().map(ItemEffect::toTemplate).collect(Collectors.toList()), quantity, position),
                    item
                ),
                Arrays.asList(
                    new InventoryEntry(player.inventory(), new PlayerItem(1, 1, 39, null, 1, -1), container.get(ItemService.class).create(39)),
                    new InventoryEntry(player.inventory(), new PlayerItem(1, 2, 40, null, 1, 1), container.get(ItemService.class).create(40))
                )
            )
        );

        assertEquals(-1, storage.find(container.get(ItemService.class).create(39)).get().position());
        assertEquals(1, storage.find(container.get(ItemService.class).create(39)).get().quantity());
        assertFalse(storage.find(container.get(ItemService.class).create(40)).isPresent());
    }

    @Test
    void deleteWillUnindexItem() throws ContainerException, InventoryException {
        Item item = container.get(ItemService.class).create(39);
        InventoryEntry entry = storage.add(item);

        assertSame(entry, storage.delete(entry));
        assertFalse(storage.find(entry.item()).isPresent());

        assertFalse(storage.iterator().hasNext());
    }

    @Test
    void deleteNotIndexedItem() throws ContainerException, InventoryException {
        Item item = container.get(ItemService.class).create(39);
        InventoryEntry entry = storage.add(item, 1, 0);

        assertSame(entry, storage.delete(entry));
        assertFalse(storage.iterator().hasNext());
    }
}
