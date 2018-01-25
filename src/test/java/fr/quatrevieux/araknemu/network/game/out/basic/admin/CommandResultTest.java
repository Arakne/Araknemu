package fr.quatrevieux.araknemu.network.game.out.basic.admin;

import fr.quatrevieux.araknemu.game.admin.LogType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandResultTest {
    @Test
    void generate() {
        assertEquals(
            "BAT0My result",
            new CommandResult(LogType.DEFAULT, "My result").toString()
        );
    }
}
