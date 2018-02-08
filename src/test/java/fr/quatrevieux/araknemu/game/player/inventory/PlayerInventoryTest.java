package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectAdded;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.item.type.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PlayerInventoryTest extends GameBaseCase {
    private Dispatcher dispatcher;
    private PlayerInventory inventory;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        inventory = new PlayerInventory(
            dispatcher = Mockito.mock(Dispatcher.class),
            dataSet.createPlayer(5),
            Collections.emptyList()
        );
    }

    @Test
    void getNotFound() {
        assertThrows(NoSuchElementException.class, () -> inventory.get(0));
    }

    @Test
    void addWillDispatchEvent() {
        InventoryEntry entry = inventory.add(new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>()));

        Mockito.verify(dispatcher).dispatch(Mockito.argThat(argument -> ObjectAdded.class.cast(argument).entry() == entry));
    }

    @Test
    void addWillCreateNewEntry() {
        Item item = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());
        InventoryEntry entry = inventory.add(item, 5);

        assertEquals(item, entry.item());
        assertEquals(5, entry.quantity());
        assertEquals(ItemEntry.DEFAULT_POSITION, entry.position());
    }

    @Test
    void addGet() {
        Item item = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());
        InventoryEntry entry = inventory.add(item, 5);

        assertSame(entry, inventory.get(entry.id()));
    }

    @Test
    void iterator() {
        Item item = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());

        List<InventoryEntry> entries = Arrays.asList(
            inventory.add(item, 5),
            inventory.add(item, 5),
            inventory.add(item, 5)
        );

        assertIterableEquals(entries, inventory);
    }

    @Test
    void createWithItems() {
        Item i1, i2;

        inventory = new PlayerInventory(
            dispatcher = Mockito.mock(Dispatcher.class),
            dataSet.createPlayer(5),
            Arrays.asList(
                new InventoryService.LoadedItem(
                    new PlayerItem(1, 2, 5, new ArrayList<>(), 5, -1),
                    i1 = Mockito.mock(Item.class)
                ),
                new InventoryService.LoadedItem(
                    new PlayerItem(1, 5, 45, new ArrayList<>(), 1, 0),
                    i2 = Mockito.mock(Item.class)
                )
            )
        );

        assertSame(i1, inventory.get(2).item());
        assertSame(i2, inventory.get(5).item());
    }
}
