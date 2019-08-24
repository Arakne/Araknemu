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
final public class BankService {
    final private ItemService itemService;
    final private AccountBankRepository bankRepository;
    final private BankItemRepository itemRepository;
    final private GameConfiguration.EconomyConfiguration configuration;

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
        Bank bank = new Bank(this, bankRepository.get(new AccountBank(account.id(), account.serverId(), 0)));

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
