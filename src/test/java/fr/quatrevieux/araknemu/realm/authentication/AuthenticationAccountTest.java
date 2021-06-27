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

package fr.quatrevieux.araknemu.realm.authentication;

import fr.quatrevieux.araknemu.common.account.banishment.BanishmentService;
import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.password.Password;
import fr.quatrevieux.araknemu.realm.authentication.password.PasswordManager;
import fr.quatrevieux.araknemu.realm.authentication.password.PlainTextHash;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationAccountTest extends RealmBaseCase {
    private AuthenticationService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new AuthenticationService(
            container.get(AccountRepository.class),
            container.get(HostService.class),
            container.get(PasswordManager.class),
            container.get(BanishmentService.class)
        );
    }

    @Test
    void accountValues() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1, "user", "pass", "pseudo", EnumSet.noneOf(Permission.class), "question", "response"),
            new PlainTextHash().parse("password"),
            service
        );

        assertEquals(1, account.id());
        assertEquals("pseudo", account.pseudo());
        assertEquals("question", account.question());
        assertEquals(0, account.community());
        assertFalse(account.isMaster());
    }

    @Test
    void password() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1, "user", "pass", "pseudo"),
            new PlainTextHash().parse("pass"),
            service
        );

        assertEquals("pass", account.password().toString());
        assertFalse(account.password().check("invalid"));
        assertTrue(account.password().check("pass"));
    }

    @Test
    void updatePassword() {
        Account entity = dataSet.push(new Account(1, "user", "pass", "pseudo", EnumSet.noneOf(Permission.class), "question", "response"));
        AuthenticationAccount account = new AuthenticationAccount(entity, new PlainTextHash().parse("pass"), service);

        Password password = new PlainTextHash().parse("newPass");
        account.updatePassword(password);

        assertSame(password, account.password());
        assertEquals("newPass", dataSet.refresh(entity).password());
    }

    @Test
    void isAlive() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1, "user", "pass", "pseudo"),
            new PlainTextHash().parse("password"),
            service
        );

        assertFalse(account.isLogged());

        account.attach(session);
        assertTrue(account.isLogged());

        session.close();
        assertFalse(account.isLogged());
    }

    @Test
    void attach() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1, "user", "pass", "pseudo"),
            new PlainTextHash().parse("password"),
            service
        );

        account.attach(session);

        assertSame(account, session.account());
        assertTrue(service.isAuthenticated(account));
    }

    @Test
    void detach() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1, "user", "pass", "pseudo"),
            new PlainTextHash().parse("password"),
            service
        );

        account.attach(session);
        account.detach();

        assertFalse(service.isAuthenticated(account));
        assertFalse(session.isLogged());
    }

    @Test
    void send() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1, "user", "pass", "pseudo"),
            new PlainTextHash().parse("password"),
            service
        );

        account.send("aaa");

        account.attach(session);

        account.send("bbb");
        requestStack.assertLast("bbb");
    }
}
