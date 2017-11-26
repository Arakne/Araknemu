package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for game accounts
 */
final public class AccountService {
    final private AccountRepository repository;

    /**
     * Accounts indexed by id
     */
    final private ConcurrentMap<Integer, GameAccount> accounts = new ConcurrentHashMap<>();

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    /**
     * Load an account
     *
     * @param account Account to load
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When cannot found the account
     */
    public GameAccount load(Account account) {
        return new GameAccount(
            repository.get(account),
            this
        );
    }

    /**
     * Set to logged accounts list
     */
    void login(GameAccount account) {
        accounts.put(account.id(), account);
    }
}
