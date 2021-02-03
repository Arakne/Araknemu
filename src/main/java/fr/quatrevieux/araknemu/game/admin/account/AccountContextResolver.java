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

package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Context resolver for account
 */
public final class AccountContextResolver implements ContextResolver {
    private final AccountService service;
    private final List<AbstractContextConfigurator<AccountContext>> configurators = new ArrayList<>();

    public AccountContextResolver(AccountService service) {
        this.service = service;
    }

    @Override
    public AccountContext resolve(Context globalContext, Object argument) throws ContextException {
        if (argument instanceof GameAccount) {
            return resolveByAccount(globalContext, GameAccount.class.cast(argument));
        }

        return resolveByPseudo(globalContext, argument.toString())
            .orElseThrow(() -> new ContextException("Invalid argument : " + argument))
        ;
    }

    @Override
    public String type() {
        return "account";
    }

    /**
     * Register a configurator for the account resolver
     */
    public AccountContextResolver register(AbstractContextConfigurator<AccountContext> configurator) {
        configurators.add(configurator);

        return this;
    }

    private AccountContext resolveByAccount(Context globalContext, GameAccount account) {
        return new AccountContext(globalContext, account, configurators);
    }

    private Optional<AccountContext> resolveByPseudo(Context globalContext, String pseudo) {
        return service.findByPseudo(pseudo).map(account -> resolveByAccount(globalContext, account));
    }
}
