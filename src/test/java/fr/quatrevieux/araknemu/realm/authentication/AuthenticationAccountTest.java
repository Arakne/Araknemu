package fr.quatrevieux.araknemu.realm.authentication;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationAccountTest extends RealmBaseCase {
    private AuthenticationService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new AuthenticationService(
            new AccountRepository(
                app.database().get("realm")
            )
        );
    }

    @Test
    void accountValues() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1, "user", "pass", "pseudo"),
            service
        );

        assertEquals(1, account.id());
        assertEquals("pseudo", account.pseudo());
        assertEquals("", account.answer());
        assertEquals(0, account.community());
        assertFalse(account.isGameMaster());
    }

    @Test
    void checkPassword() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1, "user", "pass", "pseudo"),
            service
        );

        assertFalse(account.checkPassword("invalid"));
        assertTrue(account.checkPassword("pass"));
    }

    @Test
    void isAlive() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1, "user", "pass", "pseudo"),
            service
        );

        assertFalse(account.isAlive());

        account.attach(session);
        assertTrue(account.isAlive());

        session.close();
        assertFalse(account.isAlive());
    }

    @Test
    void attach() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1, "user", "pass", "pseudo"),
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
            service
        );

        account.attach(session);
        account.detach();

        assertFalse(service.isAuthenticated(account));
        assertFalse(session.isLogged());
    }
}