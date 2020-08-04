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

import fr.quatrevieux.araknemu.common.account.banishment.event.AccountBanned;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.network.out.ServerMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
        return new GameAccount(
            repository.get(account),
            this,
            configuration.id()
        );
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
        Map<Integer, GameAccount> loadedAccounts = new HashMap<>();

        int[] toLoad = Arrays.stream(ids)
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
            loadedAccounts.put(account.id(), new GameAccount(account, this, configuration.id()));
        }

        return loadedAccounts;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<AccountBanned<GameAccount>>() {
                @Override
                public void on(AccountBanned<GameAccount> event) {
                    event.account().kick(
                        ServerMessage.kick(
                            event.banisher().map(GameAccount::pseudo).orElse("system"),
                            event.entry().cause()
                        )
                    );
                }

                @Override
                @SuppressWarnings("unchecked")
                public Class<AccountBanned<GameAccount>> event() {
                    return (Class<AccountBanned<GameAccount>>)(Class<?>) AccountBanned.class;
                }
            }
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
}
