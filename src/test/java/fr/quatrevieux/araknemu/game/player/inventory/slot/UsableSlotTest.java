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
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsableSlotTest extends GameBaseCase {
    private UsableSlot slot;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushUsableItems()
        ;

        slot = new UsableSlot(Mockito.mock(ItemStorage.class), 40);
    }

    @Test
    void checkBadClass() throws ContainerException {
        assertThrows(InventoryException.class, () -> slot.check(container.get(ItemService.class).create(39), 1));
    }

    @Test
    void checkAlreadySet() throws ContainerException {
        slot.uncheckedSet(new InventoryEntry(null, null, null));

        assertThrows(InventoryException.class, () -> slot.check(container.get(ItemService.class).create(800), 10));
    }

    @Test
    void checkSuccess() throws ContainerException, InventoryException {
        slot.check(container.get(ItemService.class).create(468), 100);
    }

    @Test
    void setFail() {
        assertThrows(InventoryException.class, () -> slot.set(
            new InventoryEntry(null, new PlayerItem(1, 1, 39, new ArrayList<>(), 1, -1), container.get(ItemService.class).create(39)
        )));

        assertFalse(slot.entry().isPresent());
    }

    @Test
    void setSuccess() throws ContainerException, InventoryException {
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 1, 468, new ArrayList<>(), 100, -1), container.get(ItemService.class).create(468));

        slot.set(entry);

        assertSame(entry, slot.entry().get());
        assertFalse(slot.equipment().isPresent());
    }

    @Test
    void equipment() {
        assertFalse(slot.equipment().isPresent());
    }
}