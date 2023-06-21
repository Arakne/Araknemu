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

package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.game.player.inventory.event.EquipmentChanged;
import fr.quatrevieux.araknemu.network.game.out.object.SpriteAccessories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendAccessoriesTest extends GameBaseCase {
    private SendAccessories listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendAccessories(
            explorationPlayer()
        );

        requestStack.clear();
    }

    @Test
    void onEquipmentChangedNotAccessory() {
        listener.on(
            new EquipmentChanged(
                new InventoryEntry(null, new PlayerItem(1, 1, 1, null, 1, 0), null),
                -1, true
            )
        );

        requestStack.assertEmpty();
    }

    @Test
    void onEquipmentChangedWithAccessoryOnEntry() throws SQLException, ContainerException {
        listener.on(
            new EquipmentChanged(
                new InventoryEntry(null, new PlayerItem(1, 1, 1, null, 1, 1), null),
                -1, true
            )
        );

        requestStack.assertLast(
            new SpriteAccessories(
                gamePlayer().id(),
                gamePlayer().inventory().accessories()
            )
        );
    }

    @Test
    void onEquipmentChangedWithAccessoryOnSlot() throws SQLException, ContainerException {
        listener.on(
            new EquipmentChanged(
                new InventoryEntry(null, new PlayerItem(1, 1, 1, null, 1, -1), null),
                1, true
            )
        );

        requestStack.assertLast(
            new SpriteAccessories(
                gamePlayer().id(),
                gamePlayer().inventory().accessories()
            )
        );
    }
}
