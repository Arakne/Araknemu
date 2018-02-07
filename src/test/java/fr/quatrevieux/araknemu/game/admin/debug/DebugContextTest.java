package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DebugContextTest extends GameBaseCase {
    private DebugContext context;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        context = new DebugContext(container);
    }

    @Test
    void commands() throws CommandNotFoundException {
        assertInstanceOf(GenItem.class, context.command("genitem"));

        assertContainsType(GenItem.class, context.commands());
    }
}
