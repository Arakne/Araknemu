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

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.Command;
import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.ContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.NullContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class AccountContextResolverTest extends GameBaseCase {
    private AccountContextResolver resolver;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(Account.class);

        resolver = new AccountContextResolver(container.get(AccountService.class));
    }

    @Test
    void resolveByGameAccount() throws ContainerException, ContextException {
        Account account = dataSet.push(new Account(-1, "aaa", "", "aaa"));

        Context context = resolver.resolve(
            new NullContext(),
            container.get(AccountService.class).load(account)
        );

        assertInstanceOf(AccountContext.class, context);
    }

    @Test
    void resolveByPseudoNotLogged() throws ContextException {
        Account account = dataSet.push(new Account(-1, "login", "", "pseudo"));
        Context context = resolver.resolve(new NullContext(), "pseudo");

        assertInstanceOf(AccountContext.class, context);
        assertEquals(account.id(), ((AccountContext) context).account().id());
        assertFalse(((AccountContext) context).account().isLogged());
    }

    @Test
    void resolveByPseudoLogged() throws ContextException {
        GameAccount account = container.get(AccountService.class).load(dataSet.push(new Account(-1, "login", "", "pseudo")));
        account.attach(server.createSession());

        Context context = resolver.resolve(new NullContext(), "pseudo");

        assertInstanceOf(AccountContext.class, context);
        assertSame(account, ((AccountContext) context).account());
        assertTrue(((AccountContext) context).account().isLogged());
    }

    @Test
    void invalidArgument() {
        assertThrows(ContextException.class, () -> resolver.resolve(new NullContext(), new Object()));
        assertThrows(ContextException.class, () -> resolver.resolve(new NullContext(), "nout_found"));
    }

    @Test
    void register() throws ContextException, CommandNotFoundException {
        Command command = Mockito.mock(Command.class);
        Mockito.when(command.name()).thenReturn("mocked");

        resolver.register(new ContextConfigurator<AccountContext>() {
            @Override
            public void configure(AccountContext context) {
                add(command);
            }
        });

        Account account = dataSet.push(new Account(-1, "aaa", "", "aaa"));

        Context context = resolver.resolve(
            new NullContext(),
            container.get(AccountService.class).load(account)
        );

        assertSame(command, context.command("mocked"));
    }
}
