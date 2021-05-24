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
import fr.quatrevieux.araknemu.game.admin.AdminPerformer;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.ContextResolver;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Context resolver for account
 */
public final class AccountContextResolver implements ContextResolver {
    private final AccountService service;
    private final Context parentContext;
    private final List<AbstractContextConfigurator<AccountContext>> configurators = new ArrayList<>();

    public AccountContextResolver(AccountService service, Context parentContext) {
        this.service = service;
        this.parentContext = parentContext;
    }

    @Override
    public AccountContext resolve(AdminPerformer performer, Supplier<String> argument) throws ContextException {
        final String arg = argument.get();

        return resolveByPseudo(arg)
            .orElseThrow(() -> new ContextException("Invalid argument : " + arg))
        ;
    }

    /**
     * Get a context from an account instance
     *
     * @param account The account instance
     *
     * @return The created context
     */
    public AccountContext resolve(GameAccount account) {
        return new AccountContext(parentContext, account, configurators);
    }

    @Override
    public char prefix() {
        return '#';
    }

    /**
     * Register a configurator for the account resolver
     */
    public AccountContextResolver register(AbstractContextConfigurator<AccountContext> configurator) {
        configurators.add(configurator);

        return this;
    }

    private Optional<AccountContext> resolveByPseudo(String pseudo) {
        return service.findByPseudo(pseudo).map(this::resolve);
    }
}
