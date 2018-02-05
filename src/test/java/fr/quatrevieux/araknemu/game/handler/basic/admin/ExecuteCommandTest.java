package fr.quatrevieux.araknemu.game.handler.basic.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.admin.LogType;
import fr.quatrevieux.araknemu.network.game.in.basic.admin.AdminCommand;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ExecuteCommandTest extends GameBaseCase {
    private ExecuteCommand handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        gamePlayer();
        gamePlayer().account().grant(Permission.ACCESS);

        handler = new ExecuteCommand(
            container.get(AdminService.class)
        );
    }

    @Test
    void handleSuccess() throws Exception {
        handler.handle(session, new AdminCommand("echo hello world"));

        requestStack.assertLast(
            new CommandResult(LogType.DEFAULT, "hello world")
        );
    }

    @Test
    void handleWithError() throws Exception {
        handler.handle(session, new AdminCommand("badCommand"));

        requestStack.assertLast(
            new CommandResult(LogType.ERROR, "Command 'badCommand' is not found")
        );
    }
}
