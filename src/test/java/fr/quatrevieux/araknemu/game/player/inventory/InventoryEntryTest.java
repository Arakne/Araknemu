package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.game.event.Listener;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectAdded;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectMoved;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.item.type.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class InventoryEntryTest extends GameBaseCase {
    private PlayerInventory inventory;
    private ListenerAggregate dispatcher;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        inventory = new PlayerInventory(
            dispatcher = new DefaultListenerAggregate(),
            dataSet.createPlayer(5),
            Collections.emptyList()
        );
    }

    @Test
    void getters() {
        Item item = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());

        InventoryEntry entry = inventory.add(item, 12, 2);

        assertEquals(1, entry.id());
        assertEquals(item, entry.item());
        assertEquals(12, entry.quantity());
        assertEquals(2, entry.position());
        assertEquals(new ArrayList<>(), entry.effects());
        assertEquals(284, entry.templateId());
    }

    @Test
    void moveNegativeQuantity() {
        InventoryEntry entry = inventory.add(new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>()));

        assertThrows(IllegalArgumentException.class, () -> entry.move(0, -5), "Invalid quantity given");
    }

    @Test
    void moveTooHighQuantity() {
        InventoryEntry entry = inventory.add(new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>()));

        assertThrows(IllegalArgumentException.class, () -> entry.move(0, 100), "Invalid quantity given");
    }

    @Test
    void moveAll() {
        InventoryEntry entry = inventory.add(new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>()));

        AtomicReference<ItemEntry> ref = new AtomicReference<>();
        dispatcher.add(ObjectMoved.class, objectMoved -> ref.set(objectMoved.entry()));

        entry.move(5, 1);

        assertSame(ref.get(), entry);
        assertEquals(5, entry.position());
    }

    @Test
    void movePartial() {
        Item item = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());
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

        entry.move(5, 3);

        assertEquals(-1, entry.position());
        assertEquals(7, entry.quantity());
        assertSame(entry, ref2.get());

        assertInstanceOf(InventoryEntry.class, ref1.get());
        assertEquals(5, ref1.get().position());
        assertEquals(3, ref1.get().quantity());
        assertSame(item, ref1.get().item());
    }

    @Test
    void add() {
        InventoryEntry entry = inventory.add(
            new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>()),
            12
        );

        AtomicReference<ObjectQuantityChanged> ref = new AtomicReference<>();
        dispatcher.add(ObjectQuantityChanged.class, ref::set);

        entry.add(3);

        assertEquals(15, entry.quantity());
        assertSame(entry, ref.get().entry());
    }

    @Test
    void remove() {
        InventoryEntry entry = inventory.add(
            new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>()),
            12
        );

        AtomicReference<ObjectQuantityChanged> ref = new AtomicReference<>();
        dispatcher.add(ObjectQuantityChanged.class, ref::set);

        entry.remove(3);

        assertEquals(9, entry.quantity());
        assertSame(entry, ref.get().entry());
    }
}
