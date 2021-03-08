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
import fr.quatrevieux.araknemu.data.living.repository.account.AccountBankRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.BankItemRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.listener.player.exchange.bank.SaveBank;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Handle the bank accounts
 */
public final class BankService {
    private final ItemService itemService;
    private final AccountBankRepository bankRepository;
    private final BankItemRepository itemRepository;
    private final GameConfiguration.EconomyConfiguration configuration;

    public BankService(ItemService itemService, AccountBankRepository bankRepository, BankItemRepository itemRepository, GameConfiguration.EconomyConfiguration configuration) {
        this.itemService = itemService;
        this.bankRepository = bankRepository;
        this.itemRepository = itemRepository;
        this.configuration = configuration;
    }

    /**
     * Load the bank for the given account
     */
    public Bank load(GameAccount account) {
        final Bank bank = new Bank(this, bankRepository.get(new AccountBank(account.id(), account.serverId(), 0)));

        bank.dispatcher().register(new SaveBank(itemRepository));

        return bank;
    }

    /**
     * Get the cost for open the bank account
     */
    public long cost(GameAccount account) {
        return cost(new AccountBank(account.id(), account.serverId(), 0));
    }

    void save(Bank bank) {
        bankRepository.add(bank.entity());
    }

    /**
     * Load bank items from the bank
     */
    List<BankEntry> loadItems(Bank bank) {
        return itemRepository
            .byBank(bank.entity())
            .stream()
            .map(entity -> new BankEntry(bank, entity, itemService.retrieve(entity.itemTemplateId(), entity.effects())))
            .collect(Collectors.toList())
        ;
    }

    /**
     * Get the cost for open the bank account
     */
    long cost(AccountBank bank) {
        return (long) (itemRepository.count(bank) * configuration.bankCostPerEntry());
    }
}
