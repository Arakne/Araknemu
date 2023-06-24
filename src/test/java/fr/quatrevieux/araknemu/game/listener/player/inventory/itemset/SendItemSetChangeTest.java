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
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.network.game.out.object.UpdateItemSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendItemSetChangeTest extends GameBaseCase {
    private SendItemSetChange listener;
    private ItemService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushItemSets().pushItemTemplates();

        service = container.get(ItemService.class);
        listener = new SendItemSetChange(gamePlayer(true));
        requestStack.clear();
    }

    @Test
    void onEquipmentChangedWithItemSet() throws SQLException, ContainerException {
        Item item = service.create(2425);

        listener.on(
            new EquipmentChanged(
                new InventoryEntry(null, null, item),
                0, true
            )
        );

        requestStack.assertLast(
            new UpdateItemSet(gamePlayer().inventory().itemSets().get(item.set().get()))
        );
    }

    @Test
    void onEquipmentChangedWithoutItemSet() throws SQLException, ContainerException {
        Item item = service.create(39);

        listener.on(
            new EquipmentChanged(
                new InventoryEntry(null, null, item),
                0, true
            )
        );

        requestStack.assertEmpty();
    }
}
