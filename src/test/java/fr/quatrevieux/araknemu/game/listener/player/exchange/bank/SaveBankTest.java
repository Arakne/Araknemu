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

package fr.quatrevieux.araknemu.game.listener.player.exchange.bank;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.data.living.repository.account.BankItemRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.bank.Bank;
import fr.quatrevieux.araknemu.game.account.bank.BankEntry;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SaveBankTest extends GameBaseCase {
    private Bank bank;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
            .use(AccountBank.class, BankItem.class)
        ;

        bank = new Bank(container.get(BankService.class), new AccountBank(1, 1, 0));
        bank.dispatcher().register(new SaveBank(container.get(BankItemRepository.class)));
    }

    @Test
    void addItem() {
        BankEntry entry = bank.add(container.get(ItemService.class).create(2422), 3);

        BankItem entity = dataSet.refresh(entry.entity());

        assertEquals(2422, entity.itemTemplateId());
        assertEquals(3, entity.quantity());
    }

    @Test
    void setQuantity() {
        BankEntry entry = bank.add(container.get(ItemService.class).create(2422), 3);
        entry.add(2);

        BankItem entity = dataSet.refresh(entry.entity());

        assertEquals(5, entity.quantity());
    }

    @Test
    void delete() {
        BankEntry entry = bank.add(container.get(ItemService.class).create(2422), 3);
        bank.delete(entry.id());

        assertThrows(EntityNotFoundException.class, () -> dataSet.refresh(entry.entity()));
    }

    @Test
    void setQuantityWillRemove() {
        BankEntry entry = bank.add(container.get(ItemService.class).create(2422), 3);
        entry.remove(3);

        assertThrows(EntityNotFoundException.class, () -> dataSet.refresh(entry.entity()));
    }
}
