package fr.quatrevieux.araknemu.realm.handler;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.network.in.SessionClosed;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;
import fr.quatrevieux.araknemu.realm.authentication.AuthenticationService;
import fr.quatrevieux.araknemu.realm.host.HostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StopSessionTest extends RealmBaseCase {
    private StopSession handler;
    private AuthenticationService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new StopSession(
            service = new AuthenticationService(
                container.get(AccountRepository.class),
                container.get(HostService.class)
            )
        );
    }

    @Test
    void handleNotLogged() {
        handler.handle(session, new SessionClosed());
    }

    @Test
    void handleWillLogout() {
        AuthenticationAccount account = new AuthenticationAccount(
            new Account(1),
            service
        );

        account.attach(session);

        handler.handle(session, new SessionClosed());

        assertFalse(session.isLogged());
        assertFalse(service.isAuthenticated(account));
    }
}