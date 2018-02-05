package fr.quatrevieux.araknemu.game.admin.exception;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.AdminService;
import fr.quatrevieux.araknemu.game.admin.LogType;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlerTest extends GameBaseCase {
    private ExceptionHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new ExceptionHandler(
            container.get(AdminService.class).user(gamePlayer())
        );
    }

    @Test
    void errors() {
        assertHandle("Command 'TEST' is not found", new CommandNotFoundException("TEST"));
        assertHandle("An error occurs during execution of 'TEST' : MY ERROR", new CommandException("TEST", "MY ERROR"));
        assertHandle("Unauthorized command 'TEST', you need at least these permissions [ACCESS, MANAGE_PLAYER]", new CommandPermissionsException("TEST", EnumSet.of(Permission.ACCESS, Permission.MANAGE_PLAYER)));
        assertHandle("Error during resolving context : MY ERROR", new ContextException("MY ERROR"));
        assertHandle("The context 'TEST' is not found", new ContextNotFoundException("TEST"));
        assertHandle("Error : java.lang.Exception: my error", new Exception("my error"));
    }

    public void assertMessage(String message) {
        requestStack.assertLast(
            new CommandResult(LogType.ERROR, message)
        );
    }

    public void assertHandle(String message, Exception e) {
        handler.handle(e);
        assertMessage(message);
    }
}