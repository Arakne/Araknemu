package fr.quatrevieux.araknemu.realm.authentication;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
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
            container.get(HostService.class)
        );
    }

    @Test
    void accountValues() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1, "user", "pass", "pseudo", EnumSet.noneOf(Permission.class), "question", "response"),
            service
        );

        assertEquals(1, account.id());
        assertEquals("pseudo", account.pseudo());
        assertEquals("question", account.question());
        assertEquals(0, account.community());
        assertFalse(account.isMaster());
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