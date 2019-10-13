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

package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.out.*;
import fr.quatrevieux.araknemu.realm.ConnectionKeyTest;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticateTest extends RealmBaseCase {
    private Authenticate handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new Authenticate(
            container.get(AuthenticationService.class),
            container.get(HostService.class)
        );

        dataSet.push(new Account(-1, "login", "password", "pseudo", EnumSet.noneOf(Permission.class), "My question", "My response"), "login_account");
    }

    @Test
    void handleSuccess() {
        handler.handle(session, new Credentials(
            "login",
            ConnectionKeyTest.cryptPassword("password", session.key().key()),
            Credentials.Method.VIGENERE_BASE_64
        ));

        assertTrue(session.isLogged());
        assertTrue(session.account().isLogged());

        requestStack.assertAll(
            new Pseudo("pseudo"),
            new Community(0),
            new GMLevel(false),
            new Question("My+question"),
            "AH1;1;110;1"
        );
    }

    @Test
    void handleSuccessWithGameMaster() throws ContainerException {
        dataSet.push(new Account(-1, "other", "password", "pseudo2", EnumSet.allOf(Permission.class), "My other question", "response"));

        handler.handle(session, new Credentials(
            "other",
            ConnectionKeyTest.cryptPassword("password", session.key().key()),
            Credentials.Method.VIGENERE_BASE_64
        ));

        assertTrue(session.isLogged());
        assertTrue(session.account().isLogged());

        requestStack.assertAll(
            new Pseudo("pseudo2"),
            new Community(0),
            new GMLevel(true),
            new Question("My+other+question"),
            "AH1;1;110;1"
        );
    }

    @Test
    void handleInvalidCredentials() {
        handler.handle(session, new Credentials(
            "login",
            ConnectionKeyTest.cryptPassword("bad_password", session.key().key()),
            Credentials.Method.VIGENERE_BASE_64
        ));

        assertFalse(session.isLogged());

        requestStack.assertLast(new LoginError(LoginError.LOGIN_ERROR));
    }

    @Test
    void handleAlreadyLoggedIn() throws ContainerException {
        AuthenticationAccount account = new AuthenticationAccount(
            (Account) dataSet.get("login_account"),
            container.get(AuthenticationService.class)
        );
        account.attach(sessionHandler.create(new DummyChannel()));

        handler.handle(session, new Credentials(
            "login",
            ConnectionKeyTest.cryptPassword("password", session.key().key()),
            Credentials.Method.VIGENERE_BASE_64
        ));

        assertFalse(session.isLogged());

        requestStack.assertLast(new LoginError(LoginError.ALREADY_LOGGED));
    }

    @Test
    void handleIsPlaying() throws ContainerException {
        connector.checkLogin = true;

        handler.handle(session, new Credentials(
            "login",
            ConnectionKeyTest.cryptPassword("password", session.key().key()),
            Credentials.Method.VIGENERE_BASE_64
        ));

        assertFalse(session.isLogged());

        requestStack.assertLast(new LoginError(LoginError.ALREADY_LOGGED_GAME_SERVER));
    }
}
