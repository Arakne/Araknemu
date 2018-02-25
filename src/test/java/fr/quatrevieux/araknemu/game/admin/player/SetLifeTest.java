package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SetLifeTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new SetLife(
            gamePlayer()
        );
    }

    @Test
    void executeWithFixedLife() throws ContainerException, SQLException, AdminException {
        gamePlayer().life().set(5);

        execute("setlife", "100");

        assertEquals(100, gamePlayer().life().current());
        assertOutput("Life of Bob is set to 100");
    }

    @Test
    void executeWithMaxLife() throws ContainerException, SQLException, AdminException {
        gamePlayer().life().set(5);

        execute("setlife", "max");

        assertTrue(gamePlayer().life().isFull());
        assertOutput("Bob retrieve all his life");
    }
}
