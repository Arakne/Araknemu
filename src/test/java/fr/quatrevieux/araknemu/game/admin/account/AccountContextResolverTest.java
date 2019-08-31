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

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.admin.Context;
import fr.quatrevieux.araknemu.game.admin.NullContext;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class AccountContextResolverTest extends GameBaseCase {
    private AccountContextResolver resolver;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.use(Account.class);

        resolver = new AccountContextResolver(
            container.get(AccountService.class),
            container.get(AccountRepository.class)
        );
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
    void invalidArgument() {
        assertThrows(ContextException.class, () -> resolver.resolve(new NullContext(), new Object()));
    }
}
