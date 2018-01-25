package fr.quatrevieux.araknemu.network.game.out.basic.admin;

import fr.quatrevieux.araknemu.game.admin.LogType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangeConsoleLineTest {
    @Test
    void generate() {
        assertEquals(
            "BAL2|1|my error",
            new ChangeConsoleLine(2, LogType.ERROR, "my error").toString()
        );
    }
}
