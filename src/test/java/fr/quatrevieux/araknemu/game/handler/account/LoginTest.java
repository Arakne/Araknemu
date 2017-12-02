package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.account.TokenService;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.exception.CloseWithPacket;
import fr.quatrevieux.araknemu.network.game.in.account.LoginToken;
import fr.quatrevieux.araknemu.network.game.out.account.LoginTokenSuccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest extends GameBaseCase {
    private Login handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new Login(
            container.get(TokenService.class),
            container.get(AccountService.class)
        );

        dataSet.use(Account.class);
    }

    @Test
    void handleSuccess() throws Exception {
        Account account = new Account(1, "test", "test", "test");
        dataSet.push(account);

        String token = container.get(TokenService.class).generate(account);

        handler.handle(session, new LoginToken(token));

        assertTrue(session.isLogged());
        requestStack.assertLast(new LoginTokenSuccess(0));
        assertEquals(1, session.account().id());
    }

    @Test
    void handleAlreadyLogged() throws ContainerException {
        session.attach(
            new GameAccount(
                new Account(1),
                container.get(AccountService.class)
            )
        );

        assertThrows(CloseImmediately.class, () -> handler.handle(session, new LoginToken("")));
    }

    @Test
    void handleBadToken() {
        assertThrows(CloseWithPacket.class, () -> handler.handle(session, new LoginToken("")));
    }

    @Test
    void handleAccountNotFound() throws ContainerException {
        String token = container.get(TokenService.class).generate(new Account(-1));

        assertThrows(CloseWithPacket.class, () -> handler.handle(session, new LoginToken(token)));
    }
}
