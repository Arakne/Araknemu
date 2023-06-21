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

package fr.quatrevieux.araknemu.game.account.bank;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectQuantityChanged;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankEntryTest extends GameBaseCase {
    private Bank bank;
    private BankEntry entry;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
            .use(AccountBank.class, BankItem.class)
        ;

        bank = container.get(BankService.class).load(explorationPlayer().account());
        entry = bank.add(container.get(ItemService.class).create(39), 3);
    }

    @Test
    void getters() {
        assertEquals(1, entry.id());
        assertEquals(39, entry.templateId());
        assertEquals(3, entry.quantity());
        assertEquals(ItemEntry.DEFAULT_POSITION, entry.position());
        assertEquals(container.get(ItemService.class).create(39), entry.item());

        assertEquals(1, entry.entity().entryId());
        assertEquals(1, entry.entity().accountId());
        assertEquals(2, entry.entity().serverId());
        assertEquals(3, entry.entity().quantity());

        assertCount(1, entry.effects());
        assertEquals(Effect.ADD_INTELLIGENCE, entry.effects().get(0).effect());
        assertEquals(2, entry.effects().get(0).min());
    }

    @Test
    void add() {
        AtomicReference<ObjectQuantityChanged> ref = new AtomicReference<>();
        bank.dispatcher().add(ObjectQuantityChanged.class, ref::set);

        entry.add(2);

        assertEquals(5, entry.quantity());
        assertSame(entry, ref.get().entry());
    }

    @Test
    void addInvalidQuantity() {
        AtomicReference<ObjectQuantityChanged> ref = new AtomicReference<>();
        bank.dispatcher().add(ObjectQuantityChanged.class, ref::set);

        assertThrows(InventoryException.class, () -> entry.add(0));
        assertThrows(InventoryException.class, () -> entry.add(-5));

        assertNull(ref.get());
        assertEquals(3, entry.quantity());
    }

    @Test
    void remove() {
        AtomicReference<ObjectQuantityChanged> ref = new AtomicReference<>();
        bank.dispatcher().add(ObjectQuantityChanged.class, ref::set);

        entry.remove(2);

        assertEquals(1, entry.quantity());
        assertSame(entry, ref.get().entry());
    }

    @Test
    void removeAll() {
        AtomicReference<ObjectDeleted> ref = new AtomicReference<>();
        bank.dispatcher().add(ObjectDeleted.class, ref::set);

        entry.remove(3);

        assertEquals(0, entry.quantity());
        assertSame(entry, ref.get().entry());
    }

    @Test
    void removeInvalidQuantity() {
        AtomicReference<ObjectQuantityChanged> ref = new AtomicReference<>();
        bank.dispatcher().add(ObjectQuantityChanged.class, ref::set);

        assertThrows(InventoryException.class, () -> entry.remove(5));

        assertNull(ref.get());
        assertEquals(3, entry.quantity());
    }
}
