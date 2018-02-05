package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NullContextTest extends TestCase {
    @Test
    void command() {
        assertThrows(CommandNotFoundException.class, () -> new NullContext().command("cmd"));
    }

    @Test
    void commands() {
        assertTrue(new NullContext().commands().isEmpty());
    }

    @Test
    void child() {
        assertThrows(ContextNotFoundException.class, () -> new NullContext().child("child"));
    }
}
