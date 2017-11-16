package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.network.realm.in.Credentials;
import fr.quatrevieux.araknemu.realm.ConnectionKey;
import fr.quatrevieux.araknemu.realm.ConnectionKeyTest;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
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

        dataSet.push(new Account(-1, "login", "password", "pseudo"));
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
    }
}
