package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddXpTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new AddXp(gamePlayer(true));
    }

    @Test
    void executeWithoutLevelUp() throws ContainerException, SQLException, AdminException {
        execute("addxp", "1000");

        assertOutput("Add 1000 xp to Bob (level = 50)");

        assertEquals(5482459, gamePlayer().properties().experience().current());
    }

    @Test
    void executeWithLevelUp() throws ContainerException, SQLException, AdminException {
        execute("addxp", "1000000");

        assertOutput("Add 1000000 xp to Bob (level = 52)");

        assertEquals(6481459, gamePlayer().properties().experience().current());
    }

    @Test
    void executeWithLongNumber() throws ContainerException, SQLException, AdminException {
        execute("addxp", "10000000000");

        assertOutput("Add 10000000000 xp to Bob (level = 200)");

        assertEquals(10005481459L, gamePlayer().properties().experience().current());
    }
}
