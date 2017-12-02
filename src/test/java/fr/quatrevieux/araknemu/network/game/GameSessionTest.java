package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import org.apache.mina.core.session.DummySession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest extends GameBaseCase {
    @Test
    void account() throws ContainerException {
        GameSession session = new GameSession(new DummySession());

        GameAccount account = new GameAccount(
            new Account(1),
            container.get(AccountService.class)
        );

        session.attach(account);
        assertSame(account, session.account());
    }
}