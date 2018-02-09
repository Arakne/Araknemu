package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class InfoTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new Info(gamePlayer());
    }

    @Test
    void execute() throws ContainerException, SQLException, AdminException {
        execute("info");

        assertOutput(
            "Player info : Bob",
            "==============================",
            "Name: Bob",
            "Race: FECA",
            "Sex: MALE",
            "Level: 50",
            "ID: 1",
            "GfxID: 10",
            "Server: 2"
        );
    }
}
