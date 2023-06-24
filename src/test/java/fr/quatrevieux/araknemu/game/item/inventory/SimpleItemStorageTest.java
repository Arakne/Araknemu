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

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.game.item.type.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleItemStorageTest extends TestCase {
    class Entry implements ItemEntry {
        final protected int id;
        final protected Item item;
        protected int quantity;
        protected int position;

        public Entry(int id, Item item, int quantity, int position) {
            this.id = id;
            this.item = item;
            this.quantity = quantity;
            this.position = position;
        }

        @Override
        public int id() {
            return id;
        }

        @Override
        public int position() {
            return position;
        }

        @Override
        public Item item() {
            return item;
        }

        @Override
        public int quantity() {
            return quantity;
        }

        @Override
        public void add(int quantity) {
            this.quantity += quantity;
        }

        @Override
        public void remove(int quantity) {
            this.quantity -= quantity;
        }

        @Override
        public List<ItemTemplateEffectEntry> effects() {
            return item.effects().stream().map(ItemEffect::toTemplate).collect(Collectors.toList());
        }

        @Override
        public int templateId() {
            return item.template().id();
        }
    }

    private SimpleItemStorage<Entry> storage;
    private Dispatcher dispatcher;

    @BeforeEach
    void setUp() {
        storage = new SimpleItemStorage<>(
            dispatcher = Mockito.mock(Dispatcher.class),
            Entry::new
        );
    }

    @Test
    void getNotFound() {
        assertThrows(ItemNotFoundException.class, () -> storage.get(0));
    }

    @Test
    void addWillDispatchEvent() throws InventoryException {
        Entry entry = storage.add(new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>()));

        Mockito.verify(dispatcher).dispatch(Mockito.argThat(argument -> ObjectAdded.class.cast(argument).entry() == entry));
    }

    @Test
    void addWillCreateNewEntry() throws InventoryException {
        Item item = new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());
        Entry entry = storage.add(item, 5);

        assertEquals(1, entry.id());
        assertEquals(item, entry.item());
        assertEquals(5, entry.quantity());
        assertEquals(ItemEntry.DEFAULT_POSITION, entry.position());
    }

    @Test
    void addWillIncrementId() throws InventoryException {
        Item item = new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());

        assertEquals(1, storage.add(item, 5).id());
        assertEquals(2, storage.add(item, 5).id());
        assertEquals(3, storage.add(item, 5).id());
    }

    @Test
    void addGet() throws InventoryException {
        Item item = new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());
        Entry entry = storage.add(item, 5);

        assertSame(entry, storage.get(1));
    }

    @Test
    void iterator() throws InventoryException {
        Item item = new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());

        List<Entry> entries = Arrays.asList(
            storage.add(item, 5),
            storage.add(item, 5),
            storage.add(item, 5)
        );

        assertIterableEquals(entries, storage);
    }

    @Test
    void createWithInitialEntries() throws InventoryException {
        List<Entry> entries = Arrays.asList(
            new Entry(5, Mockito.mock(Item.class), 2, -1),
            new Entry(9, Mockito.mock(Item.class), 1, -1),
            new Entry(12, Mockito.mock(Item.class), 1, 0)
        );

        SimpleItemStorage<Entry> storage = new SimpleItemStorage<>(
            dispatcher,
            Entry::new,
            entries
        );

        assertIterableEquals(entries, storage);

        assertSame(entries.get(0), storage.get(5));
        assertSame(entries.get(1), storage.get(9));
        assertSame(entries.get(2), storage.get(12));

        assertEquals(13, storage.add(Mockito.mock(Item.class)).id());
    }

    @Test
    void deleteNotFound() {
        SimpleItemStorage<Entry> storage = new SimpleItemStorage<>(
            dispatcher,
            Entry::new,
            new ArrayList<>()
        );

        assertThrows(ItemNotFoundException.class, () -> storage.delete(5));
    }

    @Test
    void deleteByIdSuccess() throws InventoryException {
        List<Entry> entries = Arrays.asList(
            new Entry(5, Mockito.mock(Item.class), 2, -1),
            new Entry(9, Mockito.mock(Item.class), 1, -1),
            new Entry(12, Mockito.mock(Item.class), 1, 0)
        );

        SimpleItemStorage<Entry> storage = new SimpleItemStorage<>(
            dispatcher,
            Entry::new,
            entries
        );

        assertSame(entries.get(1), storage.delete(9));
        assertIterableEquals(
            Arrays.asList(entries.get(0), entries.get(2)),
            storage
        );
    }

    @Test
    void deleteByEntrySuccess() throws InventoryException {
        List<Entry> entries = Arrays.asList(
            new Entry(5, Mockito.mock(Item.class), 2, -1),
            new Entry(9, Mockito.mock(Item.class), 1, -1),
            new Entry(12, Mockito.mock(Item.class), 1, 0)
        );

        SimpleItemStorage<Entry> storage = new SimpleItemStorage<>(
            dispatcher,
            Entry::new,
            entries
        );

        assertSame(entries.get(1), storage.delete(entries.get(1)));
        assertIterableEquals(
            Arrays.asList(entries.get(0), entries.get(2)),
            storage
        );
    }

    @Test
    void deleteWillTriggerEvent() throws InventoryException {
        List<Entry> entries = Arrays.asList(
            new Entry(5, Mockito.mock(Item.class), 2, -1),
            new Entry(9, Mockito.mock(Item.class), 1, -1),
            new Entry(12, Mockito.mock(Item.class), 1, 0)
        );

        SimpleItemStorage<Entry> storage = new SimpleItemStorage<>(
            dispatcher,
            Entry::new,
            entries
        );

        assertSame(entries.get(1), storage.delete(9));
        Mockito.verify(dispatcher).dispatch(Mockito.any(ObjectDeleted.class));
    }
}
