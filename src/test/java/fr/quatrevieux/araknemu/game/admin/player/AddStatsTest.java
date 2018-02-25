package fr.quatrevieux.araknemu.game.admin.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddStatsTest extends CommandTestCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        command = new AddStats(gamePlayer(true));
    }

    @Test
    void executeSuccess() throws ContainerException, SQLException, AdminException {
        execute("addstats", "vitality", "100");

        assertEquals(395, gamePlayer().life().max());
        assertOutput("Characteristic changed for Bob : VITALITY = 100");
    }
}
