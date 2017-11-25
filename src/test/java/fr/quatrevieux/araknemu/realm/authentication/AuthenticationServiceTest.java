package fr.quatrevieux.araknemu.realm.authentication;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

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
            container.get(HostService.class)
        );

        dataSet.use(Account.class);
    }

    @Test
    void login() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
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
            service
        );

        AuthenticationAccount account2 = new AuthenticationAccount(
            new Account(1),
            service
        );

        AuthenticationAccount account3 = new AuthenticationAccount(
            new Account(2),
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
        });

        assertEquals("invalidCredentials", response);
    }

    @Test
    void authenticateAlreadyConnected() throws ContainerException {
        Account account = dataSet.push(new Account(-1, "test", "pass", "pseudo"));

        AuthenticationAccount authenticationAccount = new AuthenticationAccount(
            account,
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
        });

        assertEquals("alreadyConnected", response);
    }

    @Test
    void authenticateIsPlaying() throws ContainerException {
        connector.checkLogin = true;
        Account account = dataSet.push(new Account(-1, "test", "pass", "pseudo"));

        AuthenticationAccount authenticationAccount = new AuthenticationAccount(
            account,
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
        });

        assertEquals("isPlaying", response);
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
        });

        assertEquals("success", response);
        assertEquals("pseudo", _account.pseudo());
    }
}