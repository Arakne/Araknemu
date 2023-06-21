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

package fr.quatrevieux.araknemu.game.player.inventory.itemset;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.BeltSlot;
import fr.quatrevieux.araknemu.game.player.inventory.slot.BootsSlot;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemSetsTest extends GameBaseCase {
    private ItemSets itemSets;
    private ItemService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        service = container.get(ItemService.class);

        itemSets = new ItemSets(
            gamePlayer(true).inventory()
        );
    }

    @Test
    void getNotPresent() {
        PlayerItemSet itemSet = itemSets.get(
            service.itemSet(1)
        );

        assertEquals(1, itemSet.id());
        assertTrue(itemSet.isEmpty());
        assertCount(0, itemSet.bonus().characteristics());
        assertCount(0, itemSet.bonus().specials());
        assertCount(0, itemSet.bonus().effects());
        assertCount(0, itemSet.items());
    }

    @Test
    void getPresent() throws SQLException, ContainerException, InventoryException {
        InventoryEntry entry1 = gamePlayer().inventory().add(service.create(2425), 1, 0);
        InventoryEntry entry2 = gamePlayer().inventory().add(service.create(2411), 1, 6);
        InventoryEntry entry3 = gamePlayer().inventory().add(service.create(2414), 1, 7);

        PlayerItemSet itemSet = itemSets.get(
            service.itemSet(1)
        );

        assertEquals(1, itemSet.id());
        assertFalse(itemSet.isEmpty());
        assertCount(2, itemSet.bonus().characteristics());
        assertCount(0, itemSet.bonus().specials());
        assertCount(2, itemSet.bonus().effects());
        assertCount(3, itemSet.items());

        assertContainsAll(
            itemSet.items(),
            entry1.item().template(),
            entry2.item().template(),
            entry3.item().template()
        );

        assertEquals(Effect.ADD_STRENGTH, itemSet.bonus().characteristics().get(0).effect());
        assertEquals(10, itemSet.bonus().characteristics().get(0).value());
        assertEquals(Effect.ADD_INTELLIGENCE, itemSet.bonus().characteristics().get(1).effect());
        assertEquals(10, itemSet.bonus().characteristics().get(1).value());
    }

    @Test
    void all() throws SQLException, ContainerException, InventoryException {
        InventoryEntry entry1 = gamePlayer().inventory().add(service.create(2425), 1, 0);
        InventoryEntry entry2 = gamePlayer().inventory().add(service.create(2414), 1, 7);
        InventoryEntry entry3 = gamePlayer().inventory().add(service.create(2641), 1, 6);
        gamePlayer().inventory().add(service.create(40), 1, 1);

        List<PlayerItemSet> sets = new ArrayList<>(itemSets.all());

        assertCount(2, sets);

        assertEquals(1, sets.get(0).id());
        assertContainsAll(
            sets.get(0).items(),
            entry1.item().template(),
            entry2.item().template()
        );

        assertEquals(7, sets.get(1).id());
        assertContainsAll(
            sets.get(1).items(),
            entry3.item().template()
        );
    }

    @Test
    void apply() throws SQLException, ContainerException, InventoryException {
        gamePlayer().inventory().add(service.create(2425), 1, 0);
        gamePlayer().inventory().add(service.create(2414), 1, 7);
        gamePlayer().inventory().add(service.create(2641), 1, 6);

        MutableCharacteristics characteristics = new DefaultCharacteristics();

        itemSets.apply(characteristics);

        assertEquals(5, characteristics.get(Characteristic.STRENGTH));
        assertEquals(5, characteristics.get(Characteristic.INTELLIGENCE));
    }

    @Test
    void applySpecials() throws SQLException, ContainerException, InventoryException {
        gamePlayer().inventory().add(service.create(8213), 1, 0);
        gamePlayer().inventory().add(service.create(8219), 1, 2);
        gamePlayer().inventory().add(service.create(8225), 1, BootsSlot.SLOT_ID);
        gamePlayer().inventory().add(service.create(8231), 1, 7);
        gamePlayer().inventory().add(service.create(8237), 1, BeltSlot.SLOT_ID);
        gamePlayer().inventory().add(service.create(8243), 1, 6);

        gamePlayer().properties().characteristics().specials().clear();
        itemSets.applySpecials(gamePlayer());

        assertEquals(60, gamePlayer().properties().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }
}
