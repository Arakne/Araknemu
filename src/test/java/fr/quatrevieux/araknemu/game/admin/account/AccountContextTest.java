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

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.admin.context.AbstractContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.NullContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class AccountContextTest extends GameBaseCase {
    private AccountContext context;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        context = new AccountContext(
            new NullContext(),
            container.get(AccountService.class).load(dataSet.push(new Account(-1, "aaa", "aaa", "aaa"))),
            Collections.singletonList(new AbstractContextConfigurator<AccountContext>() {
                @Override
                public void configure(AccountContext context) {
                    add(new Info(context.account(), container.get(AccountRepository.class)));
                }
            })
        );
    }

    @Test
    void commands() throws CommandNotFoundException {
        assertInstanceOf(Info.class, context.command("info"));
        assertInstanceOf(Grant.class, context.command("grant"));
        assertInstanceOf(Revoke.class, context.command("revoke"));

        assertContainsType(Info.class, context.commands());
        assertContainsType(Grant.class, context.commands());
        assertContainsType(Revoke.class, context.commands());
    }
}
