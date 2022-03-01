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

package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.util.RandomStringUtil;

import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handle login tokens
 */
public final class TokenService {
    private final ConcurrentMap<String, ExpirableAccount> accounts = new ConcurrentHashMap<>();
    private final RandomStringUtil randomStringUtil = new RandomStringUtil(
        new SecureRandom(),
        "abcdefghijklmnopqrstuvwxyz"
    );

    /**
     * Register an account and get token
     * @param account Account to register
     */
    public String generate(Account account) {
        final String token = generateToken();

        accounts.put(
            token,
            new ExpirableAccount(account, System.currentTimeMillis() + 30000)
        );

        return token;
    }

    /**
     * Get an account by its token and remove from service
     *
     * @param token The token
     *
     * @throws NoSuchElementException When the token cannot be found or is expired
     */
    public Account get(String token) {
        final ExpirableAccount account = accounts.remove(token);

        if (account == null) {
            throw new NoSuchElementException();
        }

        if (System.currentTimeMillis() > account.expiration) {
            throw new NoSuchElementException();
        }

        return account.account;
    }

    /**
     * Generate a random and unique token
     */
    private String generateToken() {
        String token;

        do {
            token = randomStringUtil.generate(32);
        } while (accounts.containsKey(token));

        return token;
    }

    private static class ExpirableAccount {
        private final Account account;
        private final long expiration;

        public ExpirableAccount(Account account, long expiration) {
            this.account = account;
            this.expiration = expiration;
        }
    }
}
