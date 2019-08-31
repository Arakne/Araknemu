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

package fr.quatrevieux.araknemu.game.listener.player.inventory;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.out.object.DestroyItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendItemDeletedTest extends GameBaseCase {
    private SendItemDeleted listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendItemDeleted(gamePlayer(true));

        requestStack.clear();
    }

    @Test
    void onObjectDeleted() {
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 5, 3, null, 1, 3), null);

        listener.on(
            new ObjectDeleted(entry)
        );

        requestStack.assertLast(
            new DestroyItem(entry)
        );
    }
}
