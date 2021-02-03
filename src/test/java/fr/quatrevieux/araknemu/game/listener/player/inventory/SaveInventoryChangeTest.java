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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectMoved;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SaveInventoryChangeTest extends GameBaseCase {
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(PlayerItem.class);
        dispatcher = new DefaultListenerAggregate();
        dispatcher.register(new SaveInventoryChange(container.get(InventoryService.class)));
    }

    @Test
    void onObjectDeletedBadEntry() {
        dispatcher.dispatch(new ObjectDeleted(Mockito.mock(ItemEntry.class)));
    }

    @Test
    void onObjectDeleted() throws ContainerException {
        PlayerItem entity;
        InventoryEntry entry = new InventoryEntry(
            null,
            entity = new PlayerItem(1, 1, 284, new ArrayList<>(), 5, -1),
            null
        );

        container.get(PlayerItemRepository.class).add(entity);

        dispatcher.dispatch(new ObjectDeleted(entry));

        assertFalse(container.get(PlayerItemRepository.class).has(entity));
    }

    @Test
    void onObjectMovedBadEntry() {
        dispatcher.dispatch(new ObjectMoved(Mockito.mock(ItemEntry.class)));
    }

    @Test
    void onObjectMoved() throws ContainerException {
        PlayerItem entity;
        InventoryEntry entry = new InventoryEntry(
            null,
            entity = new PlayerItem(1, 1, 284, new ArrayList<>(), 5, -1),
            null
        );

        container.get(PlayerItemRepository.class).add(entity);
        entity.setPosition(1);

        dispatcher.dispatch(new ObjectMoved(entry));

        assertEquals(1, container.get(PlayerItemRepository.class).get(entity).position());
    }

    @Test
    void onObjectQuantityChangedBadEntry() {
        dispatcher.dispatch(new ObjectQuantityChanged(Mockito.mock(ItemEntry.class)));
    }

    @Test
    void onObjectQuantityChanged() throws ContainerException {
        PlayerItem entity;
        InventoryEntry entry = new InventoryEntry(
            null,
            entity = new PlayerItem(1, 1, 284, new ArrayList<>(), 5, -1),
            null
        );

        container.get(PlayerItemRepository.class).add(entity);
        entity.setQuantity(10);

        dispatcher.dispatch(new ObjectQuantityChanged(entry));

        assertEquals(10, container.get(PlayerItemRepository.class).get(entity).quantity());
    }

    @Test
    void onObjectAddedBadEntry() {
        dispatcher.dispatch(new ObjectAdded(Mockito.mock(ItemEntry.class)));
    }

    @Test
    void onObjectAdded() throws ContainerException {
        PlayerItem entity;
        InventoryEntry entry = new InventoryEntry(
            null,
            entity = new PlayerItem(1, 1, 284, new ArrayList<>(), 5, -1),
            null
        );

        dispatcher.dispatch(new ObjectAdded(entry));

        assertTrue(container.get(PlayerItemRepository.class).has(entity));
    }
}
