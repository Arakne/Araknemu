package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.admin.NullContext;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountContextTest extends GameBaseCase {
    private AccountContext context;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        context = new AccountContext(
            new NullContext(),
            container.get(AccountService.class).load(dataSet.push(new Account(-1, "aaa", "aaa", "aaa"))),
            container.get(AccountRepository.class)
        );
    }

    @Test
    void commands() throws CommandNotFoundException {
        assertInstanceOf(Info.class, context.command("info"));

        assertContainsType(Info.class, context.commands());
    }
}
