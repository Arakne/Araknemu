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

package fr.quatrevieux.araknemu.game.player.inventory.slot;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.constraint.SlotConstraint;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.item.type.AbstractEquipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class AbstractEquipmentSlotTest extends GameBaseCase {
    private AbstractEquipmentSlot slot;
    private ListenerAggregate dispatcher;
    private ItemStorage<InventoryEntry> storage;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        storage = Mockito.mock(ItemStorage.class);
        slot = new AbstractEquipmentSlot(dispatcher = new DefaultListenerAggregate(), new SimpleSlot(1, new SlotConstraint[]{}, storage)) {
            @Override
            public Optional<AbstractEquipment> equipment() {
                return Optional.empty();
            }
        };
    }

    @Test
    void setSuccess() throws ContainerException, InventoryException {
        AtomicReference<EquipmentChanged> ref = new AtomicReference<>();
        dispatcher.add(EquipmentChanged.class, ref::set);

        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 2425, new ArrayList<>(), 1, -1), container.get(ItemService.class).create(2425));

        assertSame(entry, slot.set(entry));

        assertSame(entry, slot.entry().get());
        assertSame(entry, ref.get().entry());
        assertEquals(1, ref.get().slot());
    }

    @Test
    void setForNewEntry() throws ContainerException, InventoryException {
        AtomicReference<EquipmentChanged> ref = new AtomicReference<>();
        dispatcher.add(EquipmentChanged.class, ref::set);

        Item item = container.get(ItemService.class).create(2425, true);
        InventoryEntry entry = new InventoryEntry(null, null, null);
        Mockito.when(storage.add(item, 1, 1)).thenReturn(entry);

        assertSame(entry, slot.set(item, 1));

        assertSame(entry, slot.entry().get());
        assertSame(entry, ref.get().entry());
        assertEquals(1, ref.get().slot());
    }

    @Test
    void unset() throws ContainerException, InventoryException {
        AtomicReference<EquipmentChanged> ref = new AtomicReference<>();
        dispatcher.add(EquipmentChanged.class, ref::set);

        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 2425, new ArrayList<>(), 1, -1), container.get(ItemService.class).create(2425));
        slot.uncheckedSet(entry);

        slot.unset();

        assertFalse(slot.entry().isPresent());
        assertSame(entry, ref.get().entry());
        assertEquals(1, ref.get().slot());
    }
}
