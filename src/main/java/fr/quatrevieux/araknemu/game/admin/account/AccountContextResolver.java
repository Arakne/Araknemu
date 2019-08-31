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

package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.Context;
import fr.quatrevieux.araknemu.game.admin.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;

/**
 * Context resolver for account
 */
final public class AccountContextResolver implements ContextResolver {
    final private AccountService service;
    final private AccountRepository repository;

    public AccountContextResolver(AccountService service, AccountRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Override
    public AccountContext resolve(Context globalContext, Object argument) throws ContextException {
        if (argument instanceof GameAccount) {
            return resolveByAccount(globalContext, GameAccount.class.cast(argument));
        }

        throw new ContextException("Invalid argument : " + argument);
    }

    @Override
    public String type() {
        return "account";
    }

    private AccountContext resolveByAccount(Context globalContext, GameAccount account) {
        return new AccountContext(globalContext, account, repository);
    }
}
