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
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.exception.BadLevelException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;
import fr.quatrevieux.araknemu.game.listener.player.SendStats;
import fr.quatrevieux.araknemu.game.listener.player.inventory.SendWeight;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.game.player.inventory.itemset.PlayerItemSet;
import fr.quatrevieux.araknemu.game.player.inventory.slot.AmuletSlot;
import fr.quatrevieux.araknemu.game.player.inventory.slot.BootsSlot;
import fr.quatrevieux.araknemu.game.player.inventory.slot.HelmetSlot;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.object.AddItem;
import fr.quatrevieux.araknemu.network.game.out.object.DestroyItem;
import fr.quatrevieux.araknemu.network.game.out.object.InventoryWeight;
import fr.quatrevieux.araknemu.network.game.out.object.ItemPosition;
import fr.quatrevieux.araknemu.network.game.out.object.ItemQuantity;
import fr.quatrevieux.araknemu.network.game.out.object.SpriteAccessories;
import fr.quatrevieux.araknemu.network.game.out.object.UpdateItemSet;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellBoost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FunctionalTest extends FightBaseCase {
    private PlayerInventory inventory;
    private ItemService itemService;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        itemService = container.get(ItemService.class);
        inventory = player.inventory();
        requestStack.clear();

        // remove indirect inventory listeners
        gamePlayer().dispatcher().remove(SendStats.class);
        gamePlayer().dispatcher().remove(new SendWeight(player).listeners()[1].getClass());
    }

    @Test
    void addNewItem() throws InventoryException {
        Item item = itemService.create(284);

        InventoryEntry entry = inventory.add(item);

        assertEquals(-1, entry.position());
        assertEquals(1, entry.quantity());
        assertSame(item, entry.item());
        assertSame(entry, inventory.get(entry.id()));
        assertEquals(1, inventory.weight());

        requestStack.assertAll(
            new AddItem(entry),
            new InventoryWeight(player)
        );
    }

    @Test
    void addManyItems() throws InventoryException {
        Item item = itemService.create(284);

        InventoryEntry entry = inventory.add(item, 10);

        assertEquals(-1, entry.position());
        assertEquals(10, entry.quantity());
        assertSame(item, entry.item());
        assertSame(entry, inventory.get(entry.id()));
        assertEquals(10, inventory.weight());

        requestStack.assertAll(
            new AddItem(entry),
            new InventoryWeight(player)
        );
    }

    @Test
    void addMultipleItems() throws InventoryException {
        List<InventoryEntry> entries = Arrays.asList(
            inventory.add(itemService.create(2411)),
            inventory.add(itemService.create(2414)),
            inventory.add(itemService.create(2416))
        );

        assertIterableEquals(entries, inventory);
        assertEquals(30, inventory.weight());

        requestStack.assertAll(
            new AddItem(entries.get(0)),
            new InventoryWeight(player),
            new AddItem(entries.get(1)),
            new InventoryWeight(player),
            new AddItem(entries.get(2)),
            new InventoryWeight(player)
        );
    }

    @Test
    void moveBadQuantity() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2411));
        requestStack.clear();

        assertThrows(InventoryException.class, () -> entry.move(6, 3));

        requestStack.assertEmpty();
        assertEquals(-1, entry.position());
        assertEquals(1, entry.quantity());
    }

    @Test
    void moveBadSlot() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2411));
        requestStack.clear();

        assertThrows(InventoryException.class, () -> entry.move(1, 1));

        requestStack.assertEmpty();
        assertEquals(-1, entry.position());
        assertEquals(1, entry.quantity());
    }

    @Test
    void moveSlotFull() throws InventoryException {
        inventory.add(itemService.create(39), 1, 0);
        InventoryEntry entry = inventory.add(itemService.create(2425));
        requestStack.clear();

        assertThrows(InventoryException.class, () -> entry.move(0, 1));

        requestStack.assertEmpty();
        assertEquals(-1, entry.position());
        assertEquals(1, entry.quantity());
    }

    @Test
    void moveSuccess() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2425));
        requestStack.clear();

        entry.move(0, 1);

        requestStack.assertLast(new ItemPosition(entry));
        assertEquals(0, entry.position());
        assertEquals(1, entry.quantity());
    }

    @Test
    void moveSuccessFromStackedItem() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(39), 10);
        requestStack.clear();

        entry.move(0, 1);

        InventoryEntry newEntry = inventory.get(2);

        requestStack.assertAll(
            new AddItem(newEntry),
            new InventoryWeight(player),
            new ItemQuantity(entry),
            new InventoryWeight(player)
        );

        assertEquals(-1, entry.position());
        assertEquals(9, entry.quantity());

        assertEquals(0, newEntry.position());
        assertEquals(1, newEntry.quantity());
    }

    @Test
    void equipItem() throws InventoryException, SQLException, ContainerException {
        InventoryEntry entry = inventory.add(itemService.create(2425, true));
        requestStack.clear();

        AtomicReference<EquipmentChanged> ref = new AtomicReference<>();
        gamePlayer().dispatcher().add(EquipmentChanged.class, ref::set);

        entry.move(0, 1);

        assertNotNull(ref.get(), "EquipmentChanged should be dispatched");
        assertSame(entry, ref.get().entry());

        assertEquals(10, gamePlayer().properties().characteristics().stuff().get(Characteristic.INTELLIGENCE));
        assertEquals(10, gamePlayer().properties().characteristics().stuff().get(Characteristic.STRENGTH));
    }

    @Test
    void equipItemBadLevel() throws InventoryException, SQLException, ContainerException {
        dataSet.pushHighLevelItems();

        InventoryEntry entry = inventory.add(itemService.create(112425, true));
        requestStack.clear();

        assertThrows(BadLevelException.class, () -> entry.move(0, 1));

        assertEquals(-1, entry.position());
    }

    @Test
    void unequipItem() throws InventoryException, SQLException, ContainerException {
        InventoryEntry entry = inventory.add(itemService.create(2425, true), 1, 0);
        requestStack.clear();

        AtomicReference<EquipmentChanged> ref = new AtomicReference<>();
        gamePlayer().dispatcher().add(EquipmentChanged.class, ref::set);

        entry.move(-1, 1);

        assertNotNull(ref.get(), "EquipmentChanged should be dispatched");
        assertSame(entry, ref.get().entry());

        assertEquals(0, gamePlayer().properties().characteristics().stuff().get(Characteristic.INTELLIGENCE));
        assertEquals(0, gamePlayer().properties().characteristics().stuff().get(Characteristic.STRENGTH));
    }

    @Test
    void equipAccessoryItemOnExploration() throws InventoryException, SQLException, ContainerException {
        explorationPlayer();

        InventoryEntry entry = inventory.add(itemService.create(2411));
        requestStack.clear();

        entry.move(HelmetSlot.SLOT_ID, 1);

        requestStack.assertAll(
            new UpdateItemSet(inventory.itemSets().get(entry.item().set().get())),
            new SpriteAccessories(gamePlayer().id(), inventory.accessories()),
            new ItemPosition(entry)
        );
    }

    @Test
    void equipNotAccessoryItemOnExploration() throws InventoryException, SQLException, ContainerException {
        explorationPlayer();

        InventoryEntry entry = inventory.add(itemService.create(39));
        requestStack.clear();

        entry.move(AmuletSlot.SLOT_ID, 1);

        requestStack.assertAll(
            new ItemPosition(entry)
        );
    }

    @Test
    void addBadPosition() {
        assertThrows(InventoryException.class, () -> inventory.add(itemService.create(2425), 1, 6), "Cannot add this item to this slot");
    }

    @Test
    void deleteSimpleItem() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2425));
        requestStack.clear();

        inventory.delete(entry);

        assertThrows(ItemNotFoundException.class, () -> inventory.get(entry.id()));
        assertEquals(0, inventory.weight());
        requestStack.assertAll(
            new DestroyItem(entry),
            new InventoryWeight(player)
        );
    }

    @Test
    void deleteEquipedItem() throws InventoryException, SQLException, ContainerException {
        InventoryEntry entry = inventory.add(itemService.create(2425), 1, 0);
        requestStack.clear();


        AtomicReference<EquipmentChanged> ref = new AtomicReference<>();
        gamePlayer().dispatcher().add(EquipmentChanged.class, ref::set);

        inventory.delete(entry);

        requestStack.assertAll(
            new UpdateItemSet(inventory.itemSets().get(entry.item().set().get())),
            new DestroyItem(entry),
            new InventoryWeight(player)
        );

        assertSame(entry, ref.get().entry());
        assertFalse(ref.get().equiped());
        assertEquals(0, ref.get().slot());
        assertEquals(0, inventory.weight());
    }

    @Test
    void deleteAccessory() throws InventoryException, SQLException, ContainerException {
        explorationPlayer();

        InventoryEntry entry = inventory.add(itemService.create(2411), 1, 6);
        requestStack.clear();

        inventory.delete(entry);

        requestStack.assertAll(
            new UpdateItemSet(inventory.itemSets().get(entry.item().set().get())),
            new SpriteAccessories(gamePlayer().id(), inventory.accessories()),
            new DestroyItem(entry),
            new InventoryWeight(player)
        );
    }

    @Test
    void removeItem() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2411), 10, -1);
        requestStack.clear();

        entry.remove(3);

        assertEquals(7, entry.quantity());
        assertEquals(70, inventory.weight());

        requestStack.assertAll(
            new ItemQuantity(entry),
            new InventoryWeight(player)
        );
    }

    @Test
    void removeEntry() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2411), 10, -1);
        requestStack.clear();

        entry.remove(10);

        assertEquals(0, entry.quantity());
        assertEquals(0, inventory.weight());

        requestStack.assertAll(
            new DestroyItem(entry),
            new InventoryWeight(player)
        );
    }

    @Test
    void addWillStack() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2411, true));
        requestStack.clear();

        assertSame(entry, inventory.add(itemService.create(2411, true)));
        assertEquals(2, entry.quantity());
        assertEquals(20, inventory.weight());

        requestStack.assertAll(
            new ItemQuantity(entry),
            new InventoryWeight(player)
        );
    }

    @Test
    void moveAndStack() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2425, true));
        InventoryEntry entry2 = inventory.add(itemService.create(2425, true), 1, 0);
        assertNotSame(entry, entry2);
        requestStack.clear();

        entry2.move(-1, 1);

        assertEquals(2, entry.quantity());
        assertEquals(-1, entry.position());

        requestStack.assertAll(
            new UpdateItemSet(inventory.itemSets().get(entry2.item().set().get())),
            new DestroyItem(entry2),
            new InventoryWeight(player),
            new ItemQuantity(entry),
            new InventoryWeight(player)
        );
    }

    @Test
    void moveItemWillIndexingForStacking() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2425, true), 1, 0);
        entry.move(-1, 1);
        requestStack.clear();

        assertSame(entry, inventory.add(itemService.create(2425, true)));

        assertEquals(2, entry.quantity());
        assertEquals(-1, entry.position());
    }

    @Test
    void moveToUseBar() throws SQLException, ContainerException, InventoryException {
        dataSet.pushUsableItems();

        InventoryEntry entry = inventory.add(itemService.create(468, true), 100, -1);
        entry.move(41, 50);

        assertEquals(50, entry.quantity());
        assertEquals(-1, entry.position());

        InventoryEntry newEntry = inventory.get(2);

        assertEquals(50, newEntry.quantity());
        assertEquals(41, newEntry.position());
    }

    @Test
    void moveToUseBarNotUsable() throws SQLException, ContainerException, InventoryException {
        dataSet.pushUsableItems();

        InventoryEntry entry = inventory.add(itemService.create(2425, true), 10, -1);

        assertThrows(InventoryException.class, () -> entry.move(41, 1));

        assertEquals(10, entry.quantity());
        assertEquals(-1, entry.position());
    }

    @Test
    void equipWithItemSetAlreadySet() throws InventoryException, SQLException, ContainerException {
        inventory.add(itemService.create(2411, true), 1, HelmetSlot.SLOT_ID);
        InventoryEntry entry = inventory.add(itemService.create(2425, true));
        requestStack.clear();

        entry.move(0, 1);

        PlayerItemSet itemSet = inventory.itemSets().get(entry.item().set().get());

        assertCount(2, itemSet.items());
        assertCount(2, itemSet.bonus().characteristics());
        assertEquals(Effect.ADD_STRENGTH, itemSet.bonus().characteristics().get(0).effect());
        assertEquals(Effect.ADD_INTELLIGENCE, itemSet.bonus().characteristics().get(1).effect());

        assertEquals(55, gamePlayer().properties().characteristics().stuff().get(Characteristic.STRENGTH));
        assertEquals(55, gamePlayer().properties().characteristics().stuff().get(Characteristic.INTELLIGENCE));

        requestStack.assertOne(
            new UpdateItemSet(itemSet)
        );
    }

    @Test
    void unequipWillRemoveItemSet() throws InventoryException {
        InventoryEntry entry = inventory.add(itemService.create(2425, true), 1, AmuletSlot.SLOT_ID);
        requestStack.clear();

        entry.move(-1, 1);

        PlayerItemSet itemSet = inventory.itemSets().get(entry.item().set().get());

        assertCount(0, itemSet.items());
        assertCount(0, itemSet.bonus().characteristics());
        assertTrue(itemSet.isEmpty());

        requestStack.assertOne(
            new UpdateItemSet(itemSet)
        );
    }

    @Test
    void equipItemSetWillUpdateSpecialEffects() throws ContainerException, InventoryException, SQLException {
        inventory.add(container.get(ItemService.class).create(8213), 1, 0);
        inventory.add(container.get(ItemService.class).create(8219), 1, 2);
        inventory.add(container.get(ItemService.class).create(8225), 1, BootsSlot.SLOT_ID);
        inventory.add(container.get(ItemService.class).create(8231), 1, 7);

        InventoryEntry entry = inventory.add(container.get(ItemService.class).create(8243), 1, -1);
        entry.move(6, 1);

        assertEquals(30, gamePlayer().properties().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }

    @Test
    void unequipItemSetWillUpdateSpecialEffects() throws ContainerException, InventoryException, SQLException {
        inventory.add(container.get(ItemService.class).create(8213), 1, 0);
        inventory.add(container.get(ItemService.class).create(8219), 1, 2);
        inventory.add(container.get(ItemService.class).create(8225), 1, BootsSlot.SLOT_ID);
        inventory.add(container.get(ItemService.class).create(8231), 1, 7);
        InventoryEntry entry = inventory.add(container.get(ItemService.class).create(8243), 1, 6);
        entry.move(-1, 1);

        assertEquals(0, gamePlayer().properties().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }


    @Test
    void equipSpellModifierItem() throws InventoryException, SQLException, ContainerException {
        Item item = itemService.retrieve(39, Arrays.asList(
            new ItemTemplateEffectEntry(Effect.SPELL_ADD_DAMAGE, 3, 0, 15, "")
        ));

        InventoryEntry entry = inventory.add(item, 1, 0);

        requestStack.assertOne(new SpellBoost(3, SpellsBoosts.Modifier.DAMAGE, 15));
        assertEquals(15, gamePlayer().properties().spells().boosts().modifiers(3).damage());

        entry.move(-1, 1);

        requestStack.assertOne(new SpellBoost(3, SpellsBoosts.Modifier.DAMAGE, 0));
        assertEquals(0, gamePlayer().properties().spells().boosts().modifiers(3).damage());
    }

    @Test
    void equipAccessoryOnFightDuringPlacement() throws Exception {
        createFight();

        InventoryEntry entry = inventory.add(itemService.create(2411, true));
        requestStack.clear();

        entry.move(HelmetSlot.SLOT_ID, 1);

        requestStack.assertAll(
            new UpdateItemSet(inventory.itemSets().get(entry.item().set().get())),
            new SpriteAccessories(gamePlayer().id(), inventory.accessories()),
            new ItemPosition(entry)
        );

        assertEquals(190, player.fighter().characteristics().get(Characteristic.INTELLIGENCE));
        assertEquals(90, player.fighter().characteristics().get(Characteristic.STRENGTH));
    }

    @Test
    void unequipAccessoryOnFightDuringPlacement() throws Exception {
        InventoryEntry entry = inventory.add(itemService.create(2411, true));
        entry.move(HelmetSlot.SLOT_ID, 1);

        createFight();
        requestStack.clear();

        entry.move(ItemEntry.DEFAULT_POSITION, 1);

        requestStack.assertAll(
            new UpdateItemSet(inventory.itemSets().get(entry.item().set().get())),
            new SpriteAccessories(gamePlayer().id(), inventory.accessories()),
            new ItemPosition(entry)
        );

        assertEquals(150, player.fighter().characteristics().get(Characteristic.INTELLIGENCE));
        assertEquals(50, player.fighter().characteristics().get(Characteristic.STRENGTH));
    }

    @Test
    void addKamas() {
        assertEquals(15225, inventory.kamas());

        inventory.addKamas(300);
        assertEquals(15525, inventory.kamas());

        requestStack.assertLast(new Stats(player.properties()));
    }

    @Test
    void removeAndAddSameItem() {
        Item item = itemService.create(39);

        InventoryEntry entry = inventory.add(item);
        entry.remove(1);

        InventoryEntry newEntry = inventory.add(item);

        assertEquals(0, entry.quantity());
        assertEquals(1, newEntry.quantity());
    }
}
