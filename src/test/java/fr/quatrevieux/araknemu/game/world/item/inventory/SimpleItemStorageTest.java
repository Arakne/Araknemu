package fr.quatrevieux.araknemu.game.world.item.inventory;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectAdded;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.world.item.type.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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
        assertThrows(NoSuchElementException.class, () -> storage.get(0));
    }

    @Test
    void addWillDispatchEvent() {
        Entry entry = storage.add(new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>()));

        Mockito.verify(dispatcher).dispatch(Mockito.argThat(argument -> ObjectAdded.class.cast(argument).entry() == entry));
    }

    @Test
    void addWillCreateNewEntry() {
        Item item = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());
        Entry entry = storage.add(item, 5);

        assertEquals(1, entry.id());
        assertEquals(item, entry.item());
        assertEquals(5, entry.quantity());
        assertEquals(ItemEntry.DEFAULT_POSITION, entry.position());
    }

    @Test
    void addWillIncrementId() {
        Item item = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());

        assertEquals(1, storage.add(item, 5).id());
        assertEquals(2, storage.add(item, 5).id());
        assertEquals(3, storage.add(item, 5).id());
    }

    @Test
    void addGet() {
        Item item = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());
        Entry entry = storage.add(item, 5);

        assertSame(entry, storage.get(1));
    }

    @Test
    void iterator() {
        Item item = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());

        List<Entry> entries = Arrays.asList(
            storage.add(item, 5),
            storage.add(item, 5),
            storage.add(item, 5)
        );

        assertIterableEquals(entries, storage);
    }

    @Test
    void createWithInitialEntries() {
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
}
