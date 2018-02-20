package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.inventory.EquipmentChanged;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectAdded;
import fr.quatrevieux.araknemu.game.event.inventory.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.accessory.InventoryAccessories;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.game.world.item.type.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
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
        assertThrows(ItemNotFoundException.class, () -> inventory.get(0));
    }

    @Test
    void addWillDispatchEvent() throws InventoryException {
        InventoryEntry entry = inventory.add(new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>()));

        Mockito.verify(dispatcher).dispatch(Mockito.argThat(argument -> ObjectAdded.class.cast(argument).entry() == entry));
    }

    @Test
    void addWillCreateNewEntry() throws InventoryException {
        Item item = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());
        InventoryEntry entry = inventory.add(item, 5);

        assertEquals(item, entry.item());
        assertEquals(5, entry.quantity());
        assertEquals(ItemEntry.DEFAULT_POSITION, entry.position());
    }

    @Test
    void addGet() throws InventoryException {
        Item item = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());
        InventoryEntry entry = inventory.add(item, 5);

        assertSame(entry, inventory.get(entry.id()));
    }

    @Test
    void iterator() throws InventoryException {
        Item item1 = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());
        Item item2 = new Resource(new ItemTemplate(285, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());
        Item item3 = new Resource(new ItemTemplate(288, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());

        List<InventoryEntry> entries = Arrays.asList(
            inventory.add(item1, 5),
            inventory.add(item2, 5),
            inventory.add(item3, 5)
        );

        assertIterableEquals(entries, inventory);
    }

    @Test
    void createWithItems() throws ItemNotFoundException {
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

    @Test
    void equipmentsEmpty() {
        assertCount(0, inventory.equipments());
    }

    @Test
    void equipments() throws SQLException, ContainerException, InventoryException {
        dataSet.pushItemTemplates();

        inventory.add(container.get(ItemService.class).create(2425), 1, 0);
        inventory.add(container.get(ItemService.class).create(2416), 1, 1);
        inventory.add(container.get(ItemService.class).create(2414), 1, -1);

        assertCount(2, inventory.equipments());

        assertContains(inventory.get(1).item(), inventory.equipments());
        assertContains(inventory.get(2).item(), inventory.equipments());
    }

    @Test
    void accessories() throws SQLException, ContainerException, InventoryException {
        dataSet.pushItemTemplates();

        inventory.add(container.get(ItemService.class).create(2416), 1, 1);
        inventory.add(container.get(ItemService.class).create(2411), 1, 6);

        assertInstanceOf(InventoryAccessories.class, inventory.accessories());

        assertEquals(2416, inventory.accessories().get(AccessoryType.WEAPON).appearance());
        assertEquals(2411, inventory.accessories().get(AccessoryType.HELMET).appearance());
    }

    @Test
    void deleteWillRemoveFromSlotAndStorage() throws SQLException, ContainerException, InventoryException {
        dataSet.pushItemTemplates();

        InventoryEntry entry = inventory.add(container.get(ItemService.class).create(2416), 1, 1);

        assertSame(entry, inventory.delete(entry));

        Mockito.verify(dispatcher, Mockito.atLeastOnce()).dispatch(Mockito.any(EquipmentChanged.class));
        Mockito.verify(dispatcher, Mockito.atLeastOnce()).dispatch(Mockito.any(ObjectDeleted.class));
    }

    @Test
    void addSameItemWillStack() throws InventoryException {
        Item item = new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>());

        InventoryEntry entry = inventory.add(item, 5);

        assertSame(entry, inventory.add(item, 3));
        assertSame(entry, inventory.add(item, 12));

        assertEquals(20, entry.quantity());
        assertIterableEquals(Collections.singletonList(entry), inventory);
    }
}
