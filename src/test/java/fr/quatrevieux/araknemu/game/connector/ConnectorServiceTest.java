package fr.quatrevieux.araknemu.game.connector;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectorServiceTest extends GameBaseCase {
    private ConnectorService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new ConnectorService(
            container.get(TokenService.class),
            container.get(AccountService.class)
        );
    }

    @Test
    void isLogged() throws ContainerException {
        assertFalse(service.isLogged(1));

        GameAccount ga = new GameAccount(
            new Account(1),
            container.get(AccountService.class)
        );

        ga.attach(session);

        assertTrue(service.isLogged(1));
    }

    @Test
    void token() throws ContainerException {
        String token = service.token(1);

        assertEquals(1, container.get(TokenService.class).get(token).id());
    }
}
