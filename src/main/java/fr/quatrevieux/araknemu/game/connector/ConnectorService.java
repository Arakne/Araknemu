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

package fr.quatrevieux.araknemu.game.connector;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.TokenService;

/**
 * Service for handle realm connector requests
 */
public final class ConnectorService {
    private final TokenService tokens;
    private final AccountService accounts;

    public ConnectorService(TokenService tokens, AccountService accounts) {
        this.tokens   = tokens;
        this.accounts = accounts;
    }

    /**
     * Check if the account is logged into the game server
     */
    public boolean isLogged(int accountId) {
        return accounts.isLogged(accountId);
    }

    /**
     * Generate account token
     *
     * @param accountId Account race
     */
    public String token(int accountId) {
        return tokens.generate(new Account(accountId));
    }
}
