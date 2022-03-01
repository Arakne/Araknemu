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
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.item.inventory.SimpleItemStorage;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class InventorySlotsTest extends GameBaseCase {
    private InventorySlots slots;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        slots = new InventorySlots(
            new DefaultListenerAggregate(),
            new SimpleItemStorage<>(
                new DefaultListenerAggregate(),
                null,
                Collections.emptyList()
            ),
            gamePlayer()
        );
    }

    @Test
    void get() throws InventoryException {
        assertInstanceOf(DefaultSlot.class, slots.get(-1));
        assertInstanceOf(AmuletSlot.class, slots.get(0));
        assertInstanceOf(WeaponSlot.class, slots.get(1));
        assertInstanceOf(RingSlot.class, slots.get(2));
        assertInstanceOf(BeltSlot.class, slots.get(3));
        assertInstanceOf(RingSlot.class, slots.get(4));
        assertInstanceOf(BootsSlot.class, slots.get(5));
        assertInstanceOf(HelmetSlot.class, slots.get(6));
        assertInstanceOf(MantleSlot.class, slots.get(7));
        assertInstanceOf(NullSlot.class, slots.get(8));
        assertInstanceOf(DofusSlot.class, slots.get(9));
        assertInstanceOf(DofusSlot.class, slots.get(10));
        assertInstanceOf(DofusSlot.class, slots.get(11));
        assertInstanceOf(DofusSlot.class, slots.get(12));
        assertInstanceOf(DofusSlot.class, slots.get(13));
        assertInstanceOf(DofusSlot.class, slots.get(14));

        assertInstanceOf(UsableSlot.class, slots.get(35));
        assertInstanceOf(UsableSlot.class, slots.get(36));
        assertInstanceOf(UsableSlot.class, slots.get(37));
        assertInstanceOf(UsableSlot.class, slots.get(38));
        assertInstanceOf(UsableSlot.class, slots.get(39));
        assertInstanceOf(UsableSlot.class, slots.get(40));
        assertInstanceOf(UsableSlot.class, slots.get(41));
        assertInstanceOf(UsableSlot.class, slots.get(42));
        assertInstanceOf(UsableSlot.class, slots.get(43));
        assertInstanceOf(UsableSlot.class, slots.get(44));
        assertInstanceOf(UsableSlot.class, slots.get(45));
        assertInstanceOf(UsableSlot.class, slots.get(46));
        assertInstanceOf(UsableSlot.class, slots.get(47));
        assertInstanceOf(UsableSlot.class, slots.get(48));
        assertInstanceOf(UsableSlot.class, slots.get(49));
        assertInstanceOf(UsableSlot.class, slots.get(50));
        assertInstanceOf(UsableSlot.class, slots.get(51));
        assertInstanceOf(UsableSlot.class, slots.get(52));
        assertInstanceOf(UsableSlot.class, slots.get(53));
        assertInstanceOf(UsableSlot.class, slots.get(54));
        assertInstanceOf(UsableSlot.class, slots.get(55));
        assertInstanceOf(UsableSlot.class, slots.get(56));
        assertInstanceOf(UsableSlot.class, slots.get(57));
    }

    @Test
    void getInvalid() {
        assertThrows(InventoryException.class, () -> slots.get(-15));
        assertThrows(InventoryException.class, () -> slots.get(123));
    }

    @Test
    void equipments() throws SQLException, ContainerException, InventoryException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        slots.get(0).uncheckedSet(new InventoryEntry(null, new PlayerItem(0, 0, 2425, null, 1, 0), container.get(ItemService.class).create(2425)));
        slots.get(1).uncheckedSet(new InventoryEntry(null, new PlayerItem(0, 0, 2416, null, 1, 1), container.get(ItemService.class).create(2416)));

        assertCount(2, slots.equipments());
    }
}