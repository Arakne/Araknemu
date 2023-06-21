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

import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.exchange.BankExchangeParty;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.event.KamasChanged;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectAdded;
import fr.quatrevieux.araknemu.game.item.inventory.event.ObjectDeleted;
import fr.quatrevieux.araknemu.game.item.inventory.exception.ItemNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankTest extends GameBaseCase {
    private Bank bank;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemSets()
            .pushItemTemplates()
            .use(AccountBank.class, BankItem.class)
        ;

        bank = new Bank(container.get(BankService.class), new AccountBank(1, 1, 0));
    }

    @Test
    void kamas() {
        AtomicReference<KamasChanged> ref = new AtomicReference<>();
        bank.dispatcher().add(KamasChanged.class, ref::set);

        assertEquals(0, bank.kamas());

        bank.addKamas(15000);

        assertEquals(0, ref.get().lastQuantity());
        assertEquals(15000, ref.get().newQuantity());
        assertEquals(15000, bank.kamas());

        bank.removeKamas(5000);

        assertEquals(15000, ref.get().lastQuantity());
        assertEquals(10000, ref.get().newQuantity());
        assertEquals(10000, bank.kamas());
    }

    @Test
    void retrieveItem() {
        dataSet.push(new BankItem(1, 1, 1, 39, new ArrayList<>(), 5));
        dataSet.push(new BankItem(1, 1, 2, 40, new ArrayList<>(), 3));

        assertEquals(39, bank.get(1).templateId());
        assertEquals(5, bank.get(1).quantity());
        assertEquals(40, bank.get(2).templateId());
        assertEquals(3, bank.get(2).quantity());
    }

    @Test
    void addAndGet() {
        AtomicReference<ObjectAdded> ref = new AtomicReference<>();
        bank.dispatcher().add(ObjectAdded.class, ref::set);

        Item item = container.get(ItemService.class).create(39);
        BankEntry entry = bank.add(item, 3);

        assertEquals(item, entry.item());
        assertEquals(3, entry.quantity());
        assertSame(entry, bank.get(entry.id()));
        assertSame(entry, ref.get().entry());
    }

    @Test
    void delete() {
        AtomicReference<ObjectDeleted> ref = new AtomicReference<>();
        bank.dispatcher().add(ObjectDeleted.class, ref::set);

        BankEntry entry = bank.add(container.get(ItemService.class).create(39));

        assertSame(entry, bank.delete(entry.id()));
        assertThrows(ItemNotFoundException.class, () -> bank.get(entry.id()));
        assertSame(entry, ref.get().entry());
    }

    @Test
    void save() {
        bank.addKamas(5000);
        bank.save();

        assertEquals(5000, dataSet.refresh(bank.entity()).kamas());
    }

    @Test
    void cost() {
        dataSet.push(new BankItem(1, 1, 1, 39, new ArrayList<>(), 5));
        dataSet.push(new BankItem(1, 1, 2, 40, new ArrayList<>(), 3));

        assertEquals(2, bank.cost());
    }

    @Test
    void exchange() throws SQLException {
        assertInstanceOf(BankExchangeParty.class, bank.exchange(explorationPlayer()));
    }
}
