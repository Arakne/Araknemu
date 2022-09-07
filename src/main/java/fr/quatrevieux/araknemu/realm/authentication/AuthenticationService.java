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

package fr.quatrevieux.araknemu.realm.authentication;

import fr.quatrevieux.araknemu.common.account.banishment.BanishmentService;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.realm.authentication.password.PasswordManager;
import fr.quatrevieux.araknemu.realm.host.HostService;
import fr.quatrevieux.araknemu.realm.listener.SendUpdatedHostList;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for handle authentication
 */
public final class AuthenticationService implements EventsSubscriber {
    private final AccountRepository repository;
    private final HostService hosts;
    private final PasswordManager passwordManager;
    private final BanishmentService<AuthenticationAccount> banishmentService;

    /**
     * Set of accounts which wait for authentication process
     * There are loaded, but not yet authenticated
     */
    private final Set<AuthenticationAccount> pending = Collections.synchronizedSet(new HashSet<>());

    /**
     * Map of authenticated accounts
     * There are linked to a session
     */
    private final ConcurrentMap<Integer, AuthenticationAccount> authenticated = new ConcurrentHashMap<>();

    public AuthenticationService(AccountRepository repository, HostService hosts, PasswordManager passwordManager, BanishmentService<AuthenticationAccount> banishmentService) {
        this.repository = repository;
        this.hosts = hosts;
        this.passwordManager = passwordManager;
        this.banishmentService = banishmentService;
    }

    /**
     * Perform authenticate request
     *
     * This method is synchronized to ensure that two account are not requested login
     * in the same time
     */
    public synchronized void authenticate(AuthenticationRequest request) {
        final AuthenticationAccount account;

        try {
            account = getAccount(request.username());
        } catch (EntityNotFoundException e) {
            request.invalidCredentials();
            return;
        }

        if (banishmentService.isBanned(account)) {
            request.banned();
            return;
        }

        if (!account.password().check(request.password())) {
            request.invalidCredentials();
            return;
        }

        passwordManager.rehash(account.password(), request.password(), account::updatePassword);

        if (
            isAuthenticated(account)
            || pending.contains(account)
        ) {
            request.alreadyConnected();
            return;
        }

        pending.add(account);

        hosts.checkLogin(account, response -> {
            if (response) {
                request.isPlaying();
            } else {
                request.success(account);
            }

            pending.remove(account);
        });
    }

    /**
     * Check if the account is authenticated
     */
    public boolean isAuthenticated(AuthenticationAccount account) {
        final AuthenticationAccount alreadyAuthenticated = authenticated.get(account.id());

        return alreadyAuthenticated != null && alreadyAuthenticated.isLogged();
    }

    /**
     * Get list of all authenticated accounts
     */
    public Collection<AuthenticationAccount> authenticatedAccounts() {
        return authenticated.values();
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new SendUpdatedHostList(this),
        };
    }

    /**
     * Remove account from authenticated accounts
     */
    void logout(AuthenticationAccount account) {
        authenticated.remove(account.id());
    }

    /**
     * Add account to authenticated account
     */
    void login(AuthenticationAccount account) {
        authenticated.put(account.id(), account);
    }

    void savePassword(Account account) {
        repository.savePassword(account);
    }

    private AuthenticationAccount getAccount(String username) {
        final Account account = repository.findByUsername(username);

        return new AuthenticationAccount(account, passwordManager.get(account.password()), this);
    }
}
