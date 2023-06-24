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

package fr.quatrevieux.araknemu.game.listener.player.inventory.itemset;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.game.player.inventory.slot.BootsSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplyItemSetSpecialEffectsTest extends GameBaseCase {
    private ApplyItemSetSpecialEffects listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new ApplyItemSetSpecialEffects(
            gamePlayer(true)
        );

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;
    }

    @Test
    void onEquipmentAdded() throws SQLException, ContainerException, InventoryException {
        gamePlayer().inventory().add(container.get(ItemService.class).create(8213), 1, 0);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8219), 1, 2);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8225), 1, BootsSlot.SLOT_ID);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8231), 1, 7);
        InventoryEntry entry = gamePlayer().inventory().add(container.get(ItemService.class).create(8243), 1, 6);

        gamePlayer().properties().characteristics().specials().clear();

        listener.on(new EquipmentChanged(entry, 6, true));

        assertEquals(30, gamePlayer().properties().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }

    @Test
    void onEquipmentRemoved() throws SQLException, ContainerException, InventoryException {
        gamePlayer().inventory().add(container.get(ItemService.class).create(8213), 1, 0);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8219), 1, 2);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8225), 1, BootsSlot.SLOT_ID);
        gamePlayer().inventory().add(container.get(ItemService.class).create(8231), 1, 7);
        InventoryEntry entry = gamePlayer().inventory().add(container.get(ItemService.class).create(8243), 1, -1);

        gamePlayer().properties().characteristics().specials().add(SpecialEffects.Type.INITIATIVE, 30);

        listener.on(new EquipmentChanged(entry, 6, false));

        assertEquals(0, gamePlayer().properties().characteristics().specials().get(SpecialEffects.Type.INITIATIVE));
    }
}