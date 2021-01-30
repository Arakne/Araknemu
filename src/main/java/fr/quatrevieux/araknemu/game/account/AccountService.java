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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.listener.KickBannedAccount;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for game accounts
 */
final public class AccountService implements EventsSubscriber {
    final private AccountRepository repository;
    final private GameConfiguration configuration;

    /**
     * Accounts indexed by race
     */
    final private ConcurrentMap<Integer, GameAccount> accounts = new ConcurrentHashMap<>();

    public AccountService(AccountRepository repository, GameConfiguration configuration) {
        this.repository = repository;
        this.configuration = configuration;
    }

    /**
     * Load an account
     *
     * @param account Account to load
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When cannot found the account
     */
    public GameAccount load(Account account) {
        return instantiate(repository.get(account));
    }

    /**
     * Check if the account is logged to the game server
     * @param accountId Account race
     */
    public boolean isLogged(int accountId) {
        if (!accounts.containsKey(accountId)) {
            return false;
        }

        return accounts.get(accountId).isLogged();
    }

    /**
     * Get multiple accounts by there ids
     * If an account is already logged, the logged account will be returned
     *
     * @param ids List of account ids
     *
     * @return The loaded accounts, indexed by account id
     */
    public Map<Integer, GameAccount> getByIds(int[] ids) {
        final Map<Integer, GameAccount> loadedAccounts = new HashMap<>();
        final int[] toLoad = Arrays.stream(ids)
            .filter(id -> {
                if (accounts.containsKey(id)) {
                    loadedAccounts.put(id, accounts.get(id));
                    return false;
                }

                return true;
            })
            .toArray()
        ;

        for (Account account : repository.findByIds(toLoad)) {
            loadedAccounts.put(account.id(), instantiate(account));
        }

        return loadedAccounts;
    }

    /**
     * Find an account by its pseudo
     * If the account is logged, the logged account is returned
     *
     * @param pseudo The pseudo to search
     *
     * @return The account. If the pseudo cannot be found, an empty optional is returned
     */
    public Optional<GameAccount> findByPseudo(String pseudo) {
        // @todo need index ? actually only used by admin command
        final Optional<GameAccount> loggedAccount = accounts.values().stream()
            .filter(gameAccount -> gameAccount.pseudo().equalsIgnoreCase(pseudo))
            .findFirst()
        ;

        if (loggedAccount.isPresent()) {
            return loggedAccount;
        }

        return repository.findByPseudo(pseudo).map(this::instantiate);
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new KickBannedAccount(),
        };
    }

    /**
     * Set to logged accounts list
     */
    void login(GameAccount account) {
        accounts.put(account.id(), account);
    }

    /**
     * Remove from logged accounts list
     */
    void logout(GameAccount account) {
        accounts.remove(account.id());
    }

    /**
     * Instantiate the GameAccount for the given account entity
     */
    private GameAccount instantiate(Account entity) {
        return new GameAccount(entity, this, configuration.id());
    }
}
