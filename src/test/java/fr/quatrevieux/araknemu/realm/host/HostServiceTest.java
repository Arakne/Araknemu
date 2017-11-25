package fr.quatrevieux.araknemu.realm.host;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class HostServiceTest extends RealmBaseCase {
    private HostService service;

    private boolean checkLogin = true;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new HostService();
    }

    @Test
    void declare() {
        assertTrue(service.all().isEmpty());

        GameHost gh = new GameHost(new GameConnectorStub(), 1, 1234, "127.0.0.1");

        service.declare(gh);

        assertEquals(1, service.all().size());
        assertSame(gh, service.all().toArray()[0]);
    }

    @Test
    void updateState() {
        GameHost gh = new GameHost(new GameConnectorStub(), 1, 1234, "127.0.0.1");
        gh.setCanLog(true);
        service.declare(gh);

        service.updateHost(1, GameHost.State.SAVING, false);

        assertSame(GameHost.State.SAVING, gh.state());
        assertFalse(gh.canLog());
    }

    @Test
    void checkLoginWithNoHosts() throws ContainerException {
        service.checkLogin(
            new AuthenticationAccount(
                new Account(1),
                container.get(AuthenticationService.class)
            ),
            response -> checkLogin = response
        );

        assertFalse(checkLogin);
    }

    @Test
    void checkLoginWithOneHostsNotLogged() throws ContainerException {
        GameConnectorStub connector = new GameConnectorStub();
        GameHost gh = new GameHost(connector, 1, 1234, "127.0.0.1");
        service.declare(gh);

        connector.checkLogin = false;

        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            container.get(AuthenticationService.class)
        );

        service.checkLogin(
            account,
            response -> checkLogin = response
        );

        assertSame(account, connector.account);
        assertFalse(checkLogin);
    }

    @Test
    void checkLoginWithOneHostsLogged() throws ContainerException {
        GameConnectorStub connector = new GameConnectorStub();
        GameHost gh = new GameHost(connector, 1, 1234, "127.0.0.1");
        service.declare(gh);

        connector.checkLogin = true;

        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            container.get(AuthenticationService.class)
        );

        service.checkLogin(
            account,
            response -> checkLogin = response
        );

        assertSame(account, connector.account);
        assertTrue(checkLogin);
    }

    @Test
    void checkLoginWithMultipleHostsNotLogged() throws ContainerException {
        GameConnectorStub connector1 = new GameConnectorStub();
        GameHost gh1 = new GameHost(connector1, 1, 1234, "127.0.0.1");
        service.declare(gh1);

        GameConnectorStub connector2 = new GameConnectorStub();
        GameHost gh2 = new GameHost(connector2, 2, 4567, "127.0.0.1");
        service.declare(gh2);

        GameConnectorStub connector3 = new GameConnectorStub();
        GameHost gh3 = new GameHost(connector3, 3, 7891, "127.0.0.1");
        service.declare(gh3);

        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            container.get(AuthenticationService.class)
        );

        service.checkLogin(
            account,
            response -> checkLogin = response
        );

        assertSame(account, connector1.account);
        assertSame(account, connector2.account);
        assertSame(account, connector3.account);
        assertFalse(checkLogin);
    }

    @Test
    void checkLoginWithMultipleHostsLogged() throws ContainerException {
        GameConnectorStub connector1 = new GameConnectorStub();
        GameHost gh1 = new GameHost(connector1, 1, 1234, "127.0.0.1");
        service.declare(gh1);

        GameConnectorStub connector2 = new GameConnectorStub();
        GameHost gh2 = new GameHost(connector2, 2, 4567, "127.0.0.1");
        service.declare(gh2);
        connector2.checkLogin = true;

        GameConnectorStub connector3 = new GameConnectorStub();
        GameHost gh3 = new GameHost(connector3, 3, 7891, "127.0.0.1");
        service.declare(gh3);

        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            container.get(AuthenticationService.class)
        );

        service.checkLogin(
            account,
            response -> checkLogin = response
        );

        assertSame(account, connector1.account);
        assertSame(account, connector2.account);
        assertSame(account, connector3.account);
        assertTrue(checkLogin);
    }
}
