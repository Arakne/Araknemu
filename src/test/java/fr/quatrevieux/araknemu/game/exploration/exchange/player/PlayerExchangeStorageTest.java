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

package fr.quatrevieux.araknemu.game.exploration.exchange.player;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.AcceptChanged;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.ItemMoved;
import fr.quatrevieux.araknemu.game.exploration.exchange.event.KamasChanged;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerExchangeStorageTest extends GameBaseCase {
    private PlayerExchangeStorage storage;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        storage = new PlayerExchangeStorage(player = explorationPlayer());
    }

    @Test
    void owner() {
        assertSame(player, storage.owner());
    }

    @Test
    void kamas() {
        AtomicReference<KamasChanged> ref = new AtomicReference<>();
        storage.dispatcher().add(KamasChanged.class, ref::set);

        assertEquals(0, storage.kamas());

        storage.setKamas(100);

        assertEquals(100, storage.kamas());
        assertEquals(100, ref.get().quantity());
        assertSame(storage, ref.get().storage());
    }

    @Test
    void accepted() {
        AtomicReference<AcceptChanged> ref = new AtomicReference<>();
        storage.dispatcher().add(AcceptChanged.class, ref::set);

        assertFalse(storage.accepted());
        storage.setAccepted(true);

        assertTrue(storage.accepted());
        assertTrue(ref.get().accepted());
        assertSame(storage, ref.get().storage());

        storage.setAccepted(false);

        assertFalse(storage.accepted());
        assertFalse(ref.get().accepted());
        assertSame(storage, ref.get().storage());
    }

    @Test
    void setItem() {
        AtomicReference<ItemMoved> ref = new AtomicReference<>();
        storage.dispatcher().add(ItemMoved.class, ref::set);

        ItemEntry entry = player.inventory().add(container.get(ItemService.class).create(2422), 2);

        assertEquals(0, storage.quantity(entry));

        storage.setItem(entry, 2);

        assertEquals(2, storage.quantity(entry));
        assertEquals(1, storage.items().size());
        assertEquals(2, ref.get().quantity());
        assertSame(storage, ref.get().storage());

        storage.setItem(entry, 1);

        assertEquals(1, storage.quantity(entry));
        assertEquals(1, storage.items().size());
        assertEquals(1, ref.get().quantity());
        assertSame(storage, ref.get().storage());

        storage.setItem(entry, 0);

        assertEquals(0, storage.quantity(entry));
        assertEquals(0, storage.items().size());
        assertEquals(0, ref.get().quantity());
        assertSame(storage, ref.get().storage());
    }
}
