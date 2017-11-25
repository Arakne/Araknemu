package fr.quatrevieux.araknemu.realm.handler.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.network.realm.in.ChooseServer;
import fr.quatrevieux.araknemu.network.realm.out.SelectServerError;
import fr.quatrevieux.araknemu.network.realm.out.SelectServerPlain;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectGameTest extends RealmBaseCase {
    private ConnectGame handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new ConnectGame(
            container.get(HostService.class)
        );

        session.attach(
            new AuthenticationAccount(
                new Account(1),
                container.get(AuthenticationService.class)
            )
        );
    }

    @Test
    void handleInvalidServer() {
        handler.handle(session, new ChooseServer(10));
        requestStack.assertLast(new SelectServerError(SelectServerError.Error.CANT_SELECT));
    }

    @Test
    void handleSuccess() {
        gameHost.setCanLog(true);
        connector.token = "my_token";

        handler.handle(session, new ChooseServer(1));

        requestStack.assertLast(new SelectServerPlain("127.0.0.1", 1234, "my_token"));
        assertEquals(session.account(), connector.account);
        assertFalse(session.isAlive());
    }
}
