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
import fr.quatrevieux.araknemu.data.living.repository.account.AccountBankRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.BankItemRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BankServiceTest extends GameBaseCase {
    private BankService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
            .use(AccountBank.class, BankItem.class)
        ;

        service = new BankService(
            container.get(ItemService.class),
            container.get(AccountBankRepository.class),
            container.get(BankItemRepository.class),
            configuration.economy()
        );
    }

    @Test
    void loadNotExists() throws SQLException {
        Bank bank = service.load(explorationPlayer().account());

        assertEquals(0, bank.kamas());
        assertEquals(0, bank.stream().count());
    }

    @Test
    void loadExists() throws SQLException {
        Bank bank = service.load(explorationPlayer().account());

        bank.addKamas(5000);

        bank.add(container.get(ItemService.class).create(39), 2);
        bank.add(container.get(ItemService.class).create(2422));

        bank.save();

        bank = service.load(explorationPlayer().account());

        assertEquals(5000, bank.kamas());
        assertEquals(2, bank.stream().count());
        assertEquals(39, bank.get(1).templateId());
        assertEquals(2, bank.get(1).quantity());
        assertEquals(2422, bank.get(2).templateId());
        assertEquals(1, bank.get(2).quantity());
    }

    @Test
    void loadItems() throws SQLException {
        Bank bank = service.load(explorationPlayer().account());

        bank.addKamas(5000);

        bank.add(container.get(ItemService.class).create(39), 2);
        bank.add(container.get(ItemService.class).create(2422));

        List<BankEntry> items = service.loadItems(bank);

        assertCount(2, items);
        assertEquals(bank.get(1).item(), items.get(0).item());
        assertEquals(2, items.get(0).quantity());
        assertEquals(bank.get(2).item(), items.get(1).item());
        assertEquals(1, items.get(1).quantity());
    }

    @Test
    void costEmpty() throws SQLException {
        assertEquals(0, service.cost(explorationPlayer().account()));
    }

    @Test
    void costNotEmpty() throws SQLException {
        Bank bank = service.load(explorationPlayer().account());

        bank.add(container.get(ItemService.class).create(39), 2);
        bank.add(container.get(ItemService.class).create(2422));

        assertEquals(2, service.cost(explorationPlayer().account()));
    }
}
