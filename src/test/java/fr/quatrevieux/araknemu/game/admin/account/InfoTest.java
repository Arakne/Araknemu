package fr.quatrevieux.araknemu.game.admin.account;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class InfoTest extends CommandTestCase {
    @Test
    void executeSimple() throws ContainerException, ContextException, SQLException {
        command = new Info(
            container.get(AccountService.class).load(dataSet.push(new Account(-1, "azerty", "", "uiop"))),
            container.get(AccountRepository.class)
        );

        execute("info");

        assertOutput(
            "Account info : azerty",
            "=================================",
            "Name:   azerty",
            "Pseudo: uiop",
            "ID:     1",
            "Logged: No",
            "Standard account"
        );
    }

    @Test
    void executeLogged() throws ContainerException, ContextException, SQLException {
        GameAccount account = container.get(AccountService.class).load(dataSet.push(new Account(-1, "azerty", "", "uiop")));

        command = new Info(
            account,
            container.get(AccountRepository.class)
        );

        account.attach(new GameSession(new DummyChannel()));

        execute("info");

        assertOutputContains("Logged: Yes");
    }

    @Test
    void executeAdmin() throws ContainerException, ContextException, SQLException {
        GameAccount account = container.get(AccountService.class).load(dataSet.push(new Account(-1, "azerty", "", "uiop", EnumSet.of(Permission.ACCESS, Permission.MANAGE_ACCOUNT), "", "")));

        command = new Info(
            account,
            container.get(AccountRepository.class)
        );

        execute("info");

        assertOutputContains("Admin account");
        assertOutputContains("Permissions: [ACCESS, MANAGE_ACCOUNT]");
    }
}
