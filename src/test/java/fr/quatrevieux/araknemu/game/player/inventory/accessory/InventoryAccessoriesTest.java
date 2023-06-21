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

package fr.quatrevieux.araknemu.game.player.inventory.accessory;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.SimpleItemStorage;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.InventorySlots;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessory;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.game.world.creature.accessory.NullAccessory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InventoryAccessoriesTest extends GameBaseCase {
    private InventoryAccessories accessories;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        InventorySlots slots = new InventorySlots(
            new DefaultListenerAggregate(),
            new SimpleItemStorage<>(
                new DefaultListenerAggregate(),
                (id, item, quantity, position) -> new InventoryEntry(null, new PlayerItem(1, id, item.template().id(), null, quantity, position), item)
            ),
            gamePlayer()
        );

        slots.get(1).uncheckedSet(new InventoryEntry(null, new PlayerItem(0, 0, 2416, null, 1, 1), container.get(ItemService.class).create(2416)));
        slots.get(6).uncheckedSet(new InventoryEntry(null, new PlayerItem(0, 0, 2411, null, 1, 0), container.get(ItemService.class).create(2411)));

        accessories = new InventoryAccessories(slots);
    }

    @Test
    void getAccessory() {
        Accessory accessory = accessories.get(AccessoryType.WEAPON);

        assertInstanceOf(InventoryAccessory.class, accessory);
        assertEquals(AccessoryType.WEAPON, accessory.type());
        assertEquals(2416, accessory.appearance());
    }

    @Test
    void getNotEquiped() {
        assertInstanceOf(NullAccessory.class, accessories.get(AccessoryType.MANTLE));
    }

    @Test
    void all() {
        assertCount(5, accessories.all());

        assertEquals(2416, accessories.all().get(0).appearance());
        assertEquals(2411, accessories.all().get(1).appearance());
    }

    @Test
    void string() {
        assertEquals("970,96b,,,", accessories.toString());
    }
}
