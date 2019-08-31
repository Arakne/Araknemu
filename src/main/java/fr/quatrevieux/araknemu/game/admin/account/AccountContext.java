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
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.Context;
import fr.quatrevieux.araknemu.game.admin.SimpleContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;

import java.util.Collection;

/**
 * Context for account
 */
final public class AccountContext implements Context {
    final private Context globalContext;
    final private GameAccount account;
    final private AccountRepository repository;

    final private Context context;

    public AccountContext(Context globalContext, GameAccount account, AccountRepository repository) {
        this.globalContext = globalContext;
        this.account = account;
        this.repository = repository;

        context = configure();
    }

    @Override
    public Command command(String name) throws CommandNotFoundException {
        return context.command(name);
    }

    @Override
    public Collection<Command> commands() {
        return context.commands();
    }

    @Override
    public Context child(String name) throws ContextNotFoundException {
        return context.child(name);
    }

    private Context configure() {
        return new SimpleContext(globalContext)
            .add(new Info(account, repository))
        ;
    }
}
