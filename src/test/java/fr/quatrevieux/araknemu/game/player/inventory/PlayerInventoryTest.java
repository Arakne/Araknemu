package fr.quatrevieux.araknemu.game.player.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.game.player.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.player.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.accessory.InventoryAccessories;
import fr.quatrevieux.araknemu.game.player.inventory.itemset.PlayerItemSet;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.game.item.type.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class PlayerInventoryTest extends GameBaseCase {
    private ListenerAggregate dispatcher;
    private PlayerInventory inventory;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        inventory = new PlayerInventory(
            Collections.emptyList()
        );

        inventory.attach(gamePlayer(true));
        dispatcher = gamePlayer().dispatcher();
    }

    @Test
    void getNotFound() {
        assertThrows(ItemNotFoundException.class, () -> inventory.get(0));
    }

    @Test
    void addWillDispatchEvent() throws InventoryException {
        AtomicReference<ObjectAdded> ref = new AtomicReference<>();
        dispatcher.add(ObjectAdded.class, ref::set);

        InventoryEntry entry = inventory.add(new Resource(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ArrayList<>()));

        assertSame(entry, ref.get().entry());
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
    void createWithItems() throws ItemNotFoundException, SQLException, ContainerException {
        Item i1, i2;

        inventory = new PlayerInventory(
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

        inventory.attach(gamePlayer());

        assertSame(i1, inventory.get(2).item());
        assertSame(i2, inventory.get(5).item());
    }

    @Test
    void equipmentsEmpty() {
        assertCount(0, inventory.equipments());
    }

    @Test
    void equipments() throws SQLException, ContainerException, InventoryException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        inventory.add(container.get(ItemService.class).create(2425), 1, 0);
        inventory.add(container.get(ItemService.class).create(2416), 1, 1);
        inventory.add(container.get(ItemService.class).create(2414), 1, -1);

        assertCount(2, inventory.equipments());

        assertContains(inventory.get(1).item(), inventory.equipments());
        assertContains(inventory.get(2).item(), inventory.equipments());
    }

    @Test
    void accessories() throws SQLException, ContainerException, InventoryException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        inventory.add(container.get(ItemService.class).create(2416), 1, 1);
        inventory.add(container.get(ItemService.class).create(2411), 1, 6);

        assertInstanceOf(InventoryAccessories.class, inventory.accessories());

        assertEquals(2416, inventory.accessories().get(AccessoryType.WEAPON).appearance());
        assertEquals(2411, inventory.accessories().get(AccessoryType.HELMET).appearance());
    }

    @Test
    void deleteWillRemoveFromSlotAndStorage() throws SQLException, ContainerException, InventoryException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        AtomicReference<EquipmentChanged> ref1 = new AtomicReference<>();
        dispatcher.add(new Listener<EquipmentChanged>() {
            @Override
            public void on(EquipmentChanged event) {
                ref1.set(event);
            }

            @Override
            public Class<EquipmentChanged> event() {
                return EquipmentChanged.class;
            }
        });
        AtomicReference<ObjectDeleted> ref2 = new AtomicReference<>();
        dispatcher.add(new Listener<ObjectDeleted>() {
            @Override
            public void on(ObjectDeleted event) {
                ref2.set(event);
            }

            @Override
            public Class<ObjectDeleted> event() {
                return ObjectDeleted.class;
            }
        });

        InventoryEntry entry = inventory.add(container.get(ItemService.class).create(2416), 1, 1);

        assertSame(entry, inventory.delete(entry));
        assertSame(entry, ref1.get().entry());
        assertSame(entry, ref2.get().entry());
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

    @Test
    void owner() throws SQLException, ContainerException {
        assertSame(gamePlayer(), inventory.owner());
    }

    @Test
    void attachAlreadyAttached() {
        assertThrows(IllegalStateException.class, () -> inventory.attach(makeOtherPlayer()));
    }

    @Test
    void itemSetEmpty() {
        assertCount(0, inventory.itemSets().all());
    }

    @Test
    void itemSetNotEmpty() throws ContainerException, InventoryException, SQLException {
        dataSet.pushItemTemplates().pushItemSets();

        inventory.add(container.get(ItemService.class).create(2425), 1, 0);
        inventory.add(container.get(ItemService.class).create(2411), 1, 6);

        assertCount(1, inventory.itemSets().all());

        PlayerItemSet itemSet = inventory.itemSets().get(
            container.get(ItemService.class).itemSet(1)
        );

        assertCount(2, itemSet.items());
        assertContains(container.get(ItemTemplateRepository.class).get(2425), itemSet.items());
        assertContains(container.get(ItemTemplateRepository.class).get(2411), itemSet.items());

        assertCount(2, itemSet.bonus().characteristics());
        assertEquals(Effect.ADD_STRENGTH, itemSet.bonus().characteristics().get(0).effect());
        assertEquals(5, itemSet.bonus().characteristics().get(0).value());
        assertEquals(Effect.ADD_INTELLIGENCE, itemSet.bonus().characteristics().get(1).effect());
        assertEquals(5, itemSet.bonus().characteristics().get(1).value());
    }
}
