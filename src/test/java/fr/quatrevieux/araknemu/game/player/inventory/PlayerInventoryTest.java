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
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.event.KamasChanged;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.exception.AlreadyEquippedException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.game.item.type.Resource;
import fr.quatrevieux.araknemu.game.player.inventory.accessory.InventoryAccessories;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.game.player.inventory.itemset.PlayerItemSet;
import fr.quatrevieux.araknemu.game.player.inventory.slot.DofusSlot;
import fr.quatrevieux.araknemu.game.player.inventory.slot.RingSlot;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerInventoryTest extends GameBaseCase {
    private ListenerAggregate dispatcher;
    private PlayerInventory inventory;
    private Player player;

    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        inventory = new PlayerInventory(
            gamePlayer(true),
            player = dataSet.refresh(new Player(gamePlayer().id())),
            Collections.emptyList()
        );

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

        InventoryEntry entry = inventory.add(new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>()));

        assertSame(entry, ref.get().entry());
    }

    @Test
    void addWillCreateNewEntry() throws InventoryException {
        Item item = new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());
        InventoryEntry entry = inventory.add(item, 5);

        assertEquals(item, entry.item());
        assertEquals(5, entry.quantity());
        assertEquals(ItemEntry.DEFAULT_POSITION, entry.position());
    }

    @Test
    void addGet() throws InventoryException {
        Item item = new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());
        InventoryEntry entry = inventory.add(item, 5);

        assertSame(entry, inventory.get(entry.id()));
    }

    @Test
    void iterator() throws InventoryException {
        Item item1 = new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());
        Item item2 = new Resource(new ItemTemplate(285, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());
        Item item3 = new Resource(new ItemTemplate(288, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());

        List<InventoryEntry> entries = Arrays.asList(
            inventory.add(item1, 5),
            inventory.add(item2, 5),
            inventory.add(item3, 5)
        );

        assertIterableEquals(entries, inventory);
    }

    @Test
    void stream() throws InventoryException {
        Item item1 = new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());
        Item item2 = new Resource(new ItemTemplate(285, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());
        Item item3 = new Resource(new ItemTemplate(288, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());

        inventory.add(item1, 5);
        inventory.add(item2, 5);
        inventory.add(item3, 5);

        assertEquals(15, inventory.stream().mapToInt(InventoryEntry::quantity).sum());
    }

    @Test
    void createWithItems() throws ItemNotFoundException, SQLException, ContainerException {
        Item i1, i2;

        inventory = new PlayerInventory(
            gamePlayer(),
            dataSet.refresh(new Player(gamePlayer().id())),
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
        Item item = new Resource(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10), new ItemType(48, "Poudre", SuperType.RESOURCE, null), new ArrayList<>());

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

    @Test
    void bySlot() throws SQLException, ContainerException, InventoryException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        InventoryEntry entry = inventory.add(container.get(ItemService.class).create(2416), 1, 1);

        assertEquals(Optional.of(entry.item()), inventory.bySlot(1));
        assertFalse(inventory.bySlot(5).isPresent());
    }

    @Test
    void weight() throws SQLException, ContainerException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        assertEquals(0, inventory.weight());

        InventoryEntry entry = inventory.add(container.get(ItemService.class).create(2416), 1, 1);
        inventory.refreshWeight();

        assertEquals(10, inventory.weight());

        entry.add(5);
        inventory.refreshWeight();

        assertEquals(60, inventory.weight());

        inventory.add(container.get(ItemService.class).create(2411), 10, -1);
        inventory.refreshWeight();

        assertEquals(160, inventory.weight());
    }

    @Test
    void overweight() throws SQLException, ContainerException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        assertFalse(inventory.overweight());

        InventoryEntry entry = inventory.add(container.get(ItemService.class).create(2416), 10, -1);
        assertFalse(inventory.overweight());

        entry.add(1000);
        inventory.refreshWeight();

        assertTrue(inventory.overweight());
    }

    @Test
    void kamas() {
        player.setKamas(1450);
        assertEquals(1450, inventory.kamas());
    }

    @Test
    void addKamas() throws SQLException {
        player.setKamas(1000);

        AtomicReference<KamasChanged> ref = new AtomicReference<>();
        gamePlayer().dispatcher().add(KamasChanged.class, ref::set);

        inventory.addKamas(250);

        assertEquals(1250, inventory.kamas());
        assertEquals(1000, ref.get().lastQuantity());
        assertEquals(1250, ref.get().newQuantity());
    }

    @Test
    void addKamasWithNegativeAmountShouldRaiseException() throws SQLException {
        player.setKamas(1000);

        AtomicReference<KamasChanged> ref = new AtomicReference<>();
        gamePlayer().dispatcher().add(KamasChanged.class, ref::set);

        assertThrows(IllegalArgumentException.class, () -> inventory.addKamas(-250));

        assertEquals(1000, inventory.kamas());
        assertNull(ref.get());
    }

    @Test
    void removeKamas() throws SQLException {
        player.setKamas(1000);

        AtomicReference<KamasChanged> ref = new AtomicReference<>();
        gamePlayer().dispatcher().add(KamasChanged.class, ref::set);

        inventory.removeKamas(250);

        assertEquals(750, inventory.kamas());
        assertEquals(1000, ref.get().lastQuantity());
        assertEquals(750, ref.get().newQuantity());
    }

    @Test
    void removeKamasWithNegativeAmountShouldRaiseException() throws SQLException {
        player.setKamas(1000);

        AtomicReference<KamasChanged> ref = new AtomicReference<>();
        gamePlayer().dispatcher().add(KamasChanged.class, ref::set);

        assertThrows(IllegalArgumentException.class, () -> inventory.removeKamas(-250));

        assertEquals(1000, inventory.kamas());
        assertNull(ref.get());
    }

    /**
     * #312 https://github.com/Arakne/Araknemu/issues/312
     */
    @Test
    void equipSameDofusIsNotAllowed() throws SQLException {
        dataSet.pushItemTemplates();

        Item dofus1 = container.get(ItemService.class).create(694);
        Item dofus2 = container.get(ItemService.class).create(694);

        inventory.add(dofus1, 1, DofusSlot.SLOT_IDS[0]);
        assertThrows(AlreadyEquippedException.class, () -> inventory.add(dofus2, 1, DofusSlot.SLOT_IDS[1]));
    }

    /**
     * #312 https://github.com/Arakne/Araknemu/issues/312
     */
    @Test
    void equipSameRingIfWithinItemSetIsNotAllowed() throws SQLException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        Item ring1 = container.get(ItemService.class).create(8219);
        Item ring2 = container.get(ItemService.class).create(8219);

        inventory.add(ring1, 1, RingSlot.RING1);
        assertThrows(AlreadyEquippedException.class, () -> inventory.add(ring2, 1, RingSlot.RING2));
    }

    /**
     * #312 https://github.com/Arakne/Araknemu/issues/312
     */
    @Test
    void equipSameRingWithoutItemSetIsAllowed() throws SQLException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        Item ring1 = container.get(ItemService.class).create(849);
        Item ring2 = container.get(ItemService.class).create(849);

        inventory.add(ring1, 1, RingSlot.RING1);
        inventory.add(ring2, 1, RingSlot.RING2);

        assertCount(2, inventory.equipments());
        assertEquals(ring1, inventory.bySlot(RingSlot.RING1).get());
        assertEquals(ring1, inventory.bySlot(RingSlot.RING2).get());
    }
}
