package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest extends GameBaseCase {
    private AccountService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new AccountService(
            container.get(AccountRepository.class)
        );

        dataSet.use(Account.class);
    }

    @Test
    void loadAccountNotFound() {
        assertThrows(EntityNotFoundException.class, () -> service.load(new Account(-1)));
    }

    @Test
    void loadSuccess() throws ContainerException {
        Account account = new Account(1, "name", "pass", "pseudo");
        dataSet.push(account);

        GameAccount ga = service.load(new Account(1));

        assertEquals(1, ga.id());
    }

    @Test
    void isLogged() {
        assertFalse(service.isLogged(1));

        GameAccount account = new GameAccount(
            new Account(1),
            service
        );

        account.attach(session);
        assertTrue(service.isLogged(1));
    }
}