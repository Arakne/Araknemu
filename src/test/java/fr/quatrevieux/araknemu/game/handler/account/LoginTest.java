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

package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.core.network.exception.CloseWithPacket;
import fr.quatrevieux.araknemu.network.game.in.account.LoginToken;
import fr.quatrevieux.araknemu.network.game.out.account.LoginTokenSuccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest extends GameBaseCase {
    private Login handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new Login(
            container.get(TokenService.class),
            container.get(AccountService.class)
        );

        dataSet.use(Account.class);
    }

    @Test
    void handleSuccess() throws Exception {
        Account account = new Account(1, "test", "test", "test");
        dataSet.push(account);

        String token = container.get(TokenService.class).generate(account);

        handler.handle(session, new LoginToken(token));

        assertTrue(session.isLogged());
        requestStack.assertLast(new LoginTokenSuccess());
        assertEquals(1, session.account().id());
    }

    @Test
    void handleAlreadyLogged() throws ContainerException {
        session.attach(
            new GameAccount(
                new Account(1),
                container.get(AccountService.class),
                1
            )
        );

        assertThrows(CloseImmediately.class, () -> handler.handle(session, new LoginToken("")));
    }

    @Test
    void handleBadToken() {
        assertThrows(CloseWithPacket.class, () -> handler.handle(session, new LoginToken("")));
    }

    @Test
    void handleAccountNotFound() throws ContainerException {
        String token = container.get(TokenService.class).generate(new Account(-1));

        assertThrows(CloseWithPacket.class, () -> handler.handle(session, new LoginToken(token)));
    }
}
