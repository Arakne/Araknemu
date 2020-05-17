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

import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.ContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;

import java.util.ArrayList;
import java.util.List;

/**
 * Context resolver for account
 */
final public class AccountContextResolver implements ContextResolver {
    final private List<ContextConfigurator<AccountContext>> configurators = new ArrayList<>();

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

    /**
     * Register a configurator for the account resolver
     */
    public AccountContextResolver register(ContextConfigurator<AccountContext> configurator) {
        configurators.add(configurator);

        return this;
    }

    private AccountContext resolveByAccount(Context globalContext, GameAccount account) {
        return new AccountContext(globalContext, account, configurators);
    }
}
