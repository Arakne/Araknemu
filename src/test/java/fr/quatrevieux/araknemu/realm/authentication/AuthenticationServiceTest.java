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
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.account.Banishment;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.network.realm.out.HostList;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.password.PasswordManager;
import fr.quatrevieux.araknemu.realm.authentication.password.PlainTextHash;
import fr.quatrevieux.araknemu.realm.host.GameHost;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest extends RealmBaseCase {
    private AuthenticationService service;
    private String response;
    private AuthenticationAccount _account;

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

        dataSet.use(Account.class, Banishment.class);
    }

    @Test
    void login() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            new PlainTextHash().parse("password"),
            service
        );

        assertFalse(service.isAuthenticated(account));

        service.login(account);
        account.attach(session);

        assertTrue(service.isAuthenticated(account));
    }

    @Test
    void logout() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            new PlainTextHash().parse("password"),
            service
        );

        account.attach(session);

        service.login(account);
        assertTrue(service.isAuthenticated(account));

        service.logout(account);
        assertFalse(service.isAuthenticated(account));
    }

    @Test
    void isAuthenticated() {
        AuthenticationAccount account1 = new AuthenticationAccount(
            new Account(1),
            new PlainTextHash().parse("password"),
            service
        );

        AuthenticationAccount account2 = new AuthenticationAccount(
            new Account(1),
            new PlainTextHash().parse("password"),
            service
        );

        AuthenticationAccount account3 = new AuthenticationAccount(
            new Account(2),
            new PlainTextHash().parse("password"),
            service
        );

        assertFalse(service.isAuthenticated(account1));
        assertFalse(service.isAuthenticated(account2));
        assertFalse(service.isAuthenticated(account3));

        account1.attach(session);
        service.login(account1);

        assertTrue(service.isAuthenticated(account1));
        assertTrue(service.isAuthenticated(account2));
        assertFalse(service.isAuthenticated(account3));
    }

    @Test
    void authenticateAccountNotFound() {
        service.authenticate(new AuthenticationRequest() {
            @Override
            public String username() {
                return "not_found";
            }

            @Override
            public String password() {
                return "";
            }

            @Override
            public void success(AuthenticationAccount account) {

            }

            @Override
            public void invalidCredentials() {
                response = "invalidCredentials";
            }

            @Override
            public void alreadyConnected() {

            }

            @Override
            public void isPlaying() {

            }

            @Override
            public void banned() {

            }
        });

        assertEquals("invalidCredentials", response);
    }

    @Test
    void authenticateInvalidPassword() throws ContainerException {
        dataSet.push(new Account(-1, "test", "pass", "pseudo"));

        service.authenticate(new AuthenticationRequest() {
            @Override
            public String username() {
                return "test";
            }

            @Override
            public String password() {
                return "invalid";
            }

            @Override
            public void success(AuthenticationAccount account) {

            }

            @Override
            public void invalidCredentials() {
                response = "invalidCredentials";
            }

            @Override
            public void alreadyConnected() {

            }

            @Override
            public void isPlaying() {

            }

            @Override
            public void banned() {

            }
        });

        assertEquals("invalidCredentials", response);
    }

    @Test
    void authenticateAlreadyConnected() throws ContainerException {
        Account account = dataSet.push(new Account(-1, "test", "pass", "pseudo"));

        AuthenticationAccount authenticationAccount = new AuthenticationAccount(
            account,
            new PlainTextHash().parse("password"),
            service
        );

        authenticationAccount.attach(session);
        service.login(authenticationAccount);

        service.authenticate(new AuthenticationRequest() {
            @Override
            public String username() {
                return "test";
            }

            @Override
            public String password() {
                return "pass";
            }

            @Override
            public void success(AuthenticationAccount account) {

            }

            @Override
            public void invalidCredentials() {
                response = "invalidCredentials";
            }

            @Override
            public void alreadyConnected() {
                response = "alreadyConnected";
            }

            @Override
            public void isPlaying() {

            }

            @Override
            public void banned() {

            }
        });

        assertEquals("alreadyConnected", response);
    }

    @Test
    void authenticateIsPlaying() throws ContainerException {
        connector.checkLogin = true;
        Account account = dataSet.push(new Account(-1, "test", "pass", "pseudo"));

        AuthenticationAccount authenticationAccount = new AuthenticationAccount(
            account,
            new PlainTextHash().parse("password"),
            service
        );

        service.login(authenticationAccount);

        service.authenticate(new AuthenticationRequest() {
            @Override
            public String username() {
                return "test";
            }

            @Override
            public String password() {
                return "pass";
            }

            @Override
            public void success(AuthenticationAccount account) {

            }

            @Override
            public void invalidCredentials() {
                response = "invalidCredentials";
            }

            @Override
            public void alreadyConnected() {
                response = "alreadyConnected";
            }

            @Override
            public void isPlaying() {
                response = "isPlaying";
            }

            @Override
            public void banned() {
                response = "banned";
            }
        });

        assertEquals("isPlaying", response);
    }

    @Test
    void authenticateBanned() throws ContainerException {
        connector.checkLogin = true;
        Account account = dataSet.push(new Account(-1, "test", "pass", "pseudo"));
        dataSet.push(new Banishment(account.id(), Instant.now().minus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HOURS), "test", 3));

        AuthenticationAccount authenticationAccount = new AuthenticationAccount(
            account,
            new PlainTextHash().parse("password"),
            service
        );

        service.login(authenticationAccount);

        service.authenticate(new AuthenticationRequest() {
            @Override
            public String username() {
                return "test";
            }

            @Override
            public String password() {
                return "pass";
            }

            @Override
            public void success(AuthenticationAccount account) {

            }

            @Override
            public void invalidCredentials() {
                response = "invalidCredentials";
            }

            @Override
            public void alreadyConnected() {
                response = "alreadyConnected";
            }

            @Override
            public void isPlaying() {
                response = "isPlaying";
            }

            @Override
            public void banned() {
                response = "banned";
            }
        });

        assertEquals("banned", response);
    }

    @Test
    void authenticateSuccess() throws ContainerException {
        connector.checkLogin = false;

        dataSet.push(new Account(-1, "test", "pass", "pseudo"));

        service.authenticate(new AuthenticationRequest() {
            @Override
            public String username() {
                return "test";
            }

            @Override
            public String password() {
                return "pass";
            }

            @Override
            public void success(AuthenticationAccount account) {
                response = "success";
                _account = account;
            }

            @Override
            public void invalidCredentials() {
                response = "invalidCredentials";
            }

            @Override
            public void alreadyConnected() {
                response = "alreadyConnected";
            }

            @Override
            public void isPlaying() {
                response = "isPlaying";
            }

            @Override
            public void banned() {
                response = "banned";
            }
        });

        assertEquals("success", response);
        assertEquals("pseudo", _account.pseudo());
    }

    @Test
    void authenticateSuccessShouldRehashPassword() throws ContainerException {
        connector.checkLogin = false;

        Account entity = dataSet.push(new Account(-1, "test", "pass", "pseudo"));

        service.authenticate(new AuthenticationRequest() {
            @Override
            public String username() {
                return "test";
            }

            @Override
            public String password() {
                return "pass";
            }

            @Override
            public void success(AuthenticationAccount account) {
                response = "success";
                _account = account;
            }

            @Override
            public void invalidCredentials() {
                response = "invalidCredentials";
            }

            @Override
            public void alreadyConnected() {
                response = "alreadyConnected";
            }

            @Override
            public void isPlaying() {
                response = "isPlaying";
            }

            @Override
            public void banned() {
                response = "banned";
            }
        });

        assertEquals("success", response);
        assertEquals("pseudo", _account.pseudo());
        assertTrue(dataSet.refresh(entity).password().startsWith("$argon2id$v=19$m=65536,t=4,p=8$"));
    }

    @Test
    void authenticateSuccessWithArgon2Pass() throws ContainerException {
        connector.checkLogin = false;

        dataSet.push(new Account(-1, "test", "$argon2id$v=19$m=65536,t=4,p=8$6A7YVUL1Cs6fG/NKBnWkGg$5YXeRp8S9UOm99EzL17RyvA3HKglQkyXkaForylU8NM", "pseudo"));

        service.authenticate(new AuthenticationRequest() {
            @Override
            public String username() {
                return "test";
            }

            @Override
            public String password() {
                return "pass";
            }

            @Override
            public void success(AuthenticationAccount account) {
                response = "success";
                _account = account;
            }

            @Override
            public void invalidCredentials() {
                response = "invalidCredentials";
            }

            @Override
            public void alreadyConnected() {
                response = "alreadyConnected";
            }

            @Override
            public void isPlaying() {
                response = "isPlaying";
            }

            @Override
            public void banned() {
                response = "banned";
            }
        });

        assertEquals("success", response);
        assertEquals("pseudo", _account.pseudo());
    }

    @Test
    void listenerOnHostUpdatedShouldSendToAuthenticatedAccountsHostsList() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            new PlainTextHash().parse("password"),
            service
        );
        service.login(account);
        account.attach(session);

        container.get(ListenerAggregate.class).register(service);
        container.get(HostService.class).declare(new GameHost(null, 2, 1234, "127.0.0.1"));

        requestStack.assertLast("AH1;1;110;1|2;0;110;0");

        container.get(HostService.class).updateHost(2, GameHost.State.ONLINE, true);
        requestStack.assertLast("AH1;1;110;1|2;1;110;1");
    }
}
