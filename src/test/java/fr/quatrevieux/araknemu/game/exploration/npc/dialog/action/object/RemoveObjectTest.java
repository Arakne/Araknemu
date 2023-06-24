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

package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.object;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RemoveObjectTest extends GameBaseCase {
    private ExplorationPlayer player;
    private RemoveObject.Factory factory;
    private ItemService service;
    private InventoryEntry previousEntry;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        service = container.get(ItemService.class);
        player = explorationPlayer();
        factory = new RemoveObject.Factory();

        previousEntry = player.inventory().add(service.create(39), 2);
    }

    @Override
    @AfterEach
    public void tearDown() throws ContainerException {
        // Ensure that previous entries are not removed
        assertEquals(2, previousEntry.quantity());

        super.tearDown();
    }

    @Test
    void factory() {
        assertInstanceOf(RemoveObject.class, factory.create(new ResponseAction(1, "REM_OBJECT", "123")));
    }

    @Test
    void checkObjectNotFound() {
        assertFalse(factory.create(new ResponseAction(1, "REM_OBJECT", "2425")).check(player));
    }

    @Test
    void checkBadQuantity() {
        player.inventory().add(service.create(2425), 3);

        assertFalse(factory.create(new ResponseAction(1, "REM_OBJECT", "2425,5")).check(player));
    }

    @Test
    void checkSuccessOne() {
        player.inventory().add(service.create(2425));

        assertTrue(factory.create(new ResponseAction(1, "REM_OBJECT", "2425")).check(player));
    }

    @Test
    void checkSuccessManyOnSingleEntry() {
        player.inventory().add(service.create(2425), 5);

        assertTrue(factory.create(new ResponseAction(1, "REM_OBJECT", "2425,5")).check(player));
    }

    @Test
    void checkSuccessManyOnMultipleEntry() {
        player.inventory().add(service.create(2425), 3);
        player.inventory().add(service.create(2425), 4);

        assertTrue(factory.create(new ResponseAction(1, "REM_OBJECT", "2425,5")).check(player));
    }

    @Test
    void checkSuccessNotRequired() {
        assertTrue(factory.create(new ResponseAction(1, "REM_OBJECT", "2425,5,0")).check(player));
    }

    @Test
    void applyItemNotFounds() {
        factory.create(new ResponseAction(1, "REM_OBJECT", "2425,5,0")).apply(player);
    }

    @Test
    void applyRemoveSingleObject() {
        InventoryEntry entry = player.inventory().add(service.create(2425));

        factory.create(new ResponseAction(1, "REM_OBJECT", "2425")).apply(player);

        assertEquals(0, entry.quantity());
    }

    @Test
    void applyRemoveTheRequiredQuantity() {
        InventoryEntry entry = player.inventory().add(service.create(2425), 5);

        factory.create(new ResponseAction(1, "REM_OBJECT", "2425,3")).apply(player);

        assertEquals(2, entry.quantity());
    }

    @Test
    void applyRemoveOnMultipleEntries() {
        InventoryEntry entry1 = player.inventory().add(service.create(2425), 3);
        InventoryEntry entry2 = player.inventory().add(service.create(2425), 4);

        factory.create(new ResponseAction(1, "REM_OBJECT", "2425,5")).apply(player);

        assertEquals(0, entry1.quantity());
        assertEquals(2, entry2.quantity());
    }

    @Test
    void applyRemoveNotEnough() {
        InventoryEntry entry = player.inventory().add(service.create(2425), 3);

        factory.create(new ResponseAction(1, "REM_OBJECT", "2425,5,0")).apply(player);

        assertEquals(0, entry.quantity());
    }

    @Test
    void applyWithZeroQuantityShouldBeIgnored() {
        InventoryEntry entry = player.inventory().add(service.create(2425), 0);

        requestStack.clear();
        factory.create(new ResponseAction(1, "REM_OBJECT", "2425")).apply(player);

        requestStack.assertEmpty();
        assertEquals(0, entry.quantity());
    }
}
