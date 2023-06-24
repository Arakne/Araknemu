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

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.inventory.event.KamasChanged;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectMoved;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.object.AddItem;
import fr.quatrevieux.araknemu.network.game.out.object.DestroyItem;
import fr.quatrevieux.araknemu.network.game.out.object.ItemPosition;
import fr.quatrevieux.araknemu.network.game.out.object.ItemQuantity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

class SendInventoryUpdateTest extends GameBaseCase {
    private ListenerAggregate dispatcher;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        dispatcher = new DefaultListenerAggregate();
        dispatcher.register(new SendInventoryUpdate(gamePlayer()));
    }

    @Test
    void onObjectAdded() {
        InventoryEntry entry = new InventoryEntry(
            null,
            new PlayerItem(1, 1, 284, new ArrayList<>(), 5, -1),
            null
        );

        dispatcher.dispatch(new ObjectAdded(entry));

        requestStack.assertLast(new AddItem(entry));
    }

    @Test
    void onObjectDeleted() {
        InventoryEntry entry = new InventoryEntry(null, new PlayerItem(1, 5, 3, null, 1, 3), null);

        dispatcher.dispatch(new ObjectDeleted(entry));

        requestStack.assertLast(
            new DestroyItem(entry)
        );
    }

    @Test
    void onObjectMoved() {
        InventoryEntry entry = new InventoryEntry(
            null,
            new PlayerItem(1, 1, 284, new ArrayList<>(), 5, 1),
            null
        );

        dispatcher.dispatch(new ObjectMoved(entry));

        requestStack.assertLast(
            new ItemPosition(entry)
        );
    }

    @Test
    void onObjectQuantityChanged() {
        InventoryEntry entry = new InventoryEntry(
            null,
            new PlayerItem(1, 1, 284, new ArrayList<>(), 5, -1),
            null
        );

        dispatcher.dispatch(new ObjectQuantityChanged(entry));

        requestStack.assertLast(
            new ItemQuantity(entry)
        );
    }

    @Test
    void onKamasChanged() throws SQLException {
        dispatcher.dispatch(new KamasChanged(0, 15225));

        requestStack.assertLast(new Stats(gamePlayer().properties()));
    }
}
