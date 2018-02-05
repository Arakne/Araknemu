package fr.quatrevieux.araknemu.game.admin.global;

import fr.quatrevieux.araknemu.game.admin.CommandTestCase;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalContextTest extends CommandTestCase {
    private GlobalContext context;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        context = new GlobalContext(user());
    }

    @Test
    void commands() throws CommandNotFoundException {
        assertInstanceOf(Echo.class, context.command("echo"));

        assertContainsType(Echo.class, context.commands());
    }
}
