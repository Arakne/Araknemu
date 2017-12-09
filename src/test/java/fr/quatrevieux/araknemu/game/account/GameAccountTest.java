package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameAccountTest extends GameBaseCase {
    @Test
    void isLogged() throws ContainerException {
        GameAccount account = new GameAccount(
            new Account(-1),
            container.get(AccountService.class),
            1
        );

        assertFalse(account.isLogged());
        account.attach(session);
        assertTrue(account.isLogged());
    }

    @Test
    void attachDetach() throws ContainerException {
        GameAccount account = new GameAccount(
            new Account(-1),
            container.get(AccountService.class),
            1
        );

        account.attach(session);
        account.detach();
        assertFalse(account.isLogged());
    }

    @Test
    void getters() throws ContainerException {
        GameAccount account = new GameAccount(
            new Account(1),
            container.get(AccountService.class),
            1
        );

        assertEquals(1, account.id());
        assertEquals(0, account.community());
    }
}