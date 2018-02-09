package fr.quatrevieux.araknemu.game.admin.global;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class EchoTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Echo();
    }

    @Test
    void executeSimple() throws ContainerException, SQLException, AdminException {
        execute("echo", "Hello", "World", "!");

        assertInfo("Hello World !");
    }

    @Test
    void executeInfo() throws ContainerException, SQLException, AdminException {
        execute("echo", "-i", "Hello", "World", "!");

        assertInfo("Hello World !");
    }

    @Test
    void executeSuccess() throws ContainerException, SQLException, AdminException {
        execute("echo", "-s", "Hello", "World", "!");

        assertSuccess("Hello World !");
    }

    @Test
    void executeError() throws ContainerException, SQLException, AdminException {
        execute("echo", "-e", "Hello", "World", "!");

        assertError("Hello World !");
    }
}