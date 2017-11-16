package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.network.realm.RealmSession;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.network.realm.out.*;
import fr.quatrevieux.araknemu.realm.ConnectionKeyTest;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import org.apache.mina.core.session.DummySession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticateTest extends RealmBaseCase {
    private Authenticate handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new Authenticate(
            container.get(AuthenticationService.class)
        );

        dataSet.push(new Account(-1, "login", "password", "pseudo"), "login_account");
    }

    @Test
    void handleSuccess() {
        handler.handle(session, new Credentials(
            "login",
            ConnectionKeyTest.cryptPassword("password", session.key().key()),
            Credentials.Method.VIGENERE_BASE_64
        ));

        assertTrue(session.isLogged());
        assertTrue(session.account().isAlive());

        requestStack.assertAll(
            new Pseudo("pseudo"),
            new Community(0),
            new GMLevel(false),
            new Answer("")
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
        account.attach(new RealmSession(new DummySession()));

        handler.handle(session, new Credentials(
            "login",
            ConnectionKeyTest.cryptPassword("password", session.key().key()),
            Credentials.Method.VIGENERE_BASE_64
        ));

        assertFalse(session.isLogged());

        requestStack.assertLast(new LoginError(LoginError.ALREADY_LOGGED));
    }
}
