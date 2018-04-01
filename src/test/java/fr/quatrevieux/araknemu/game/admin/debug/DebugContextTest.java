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
        assertInstanceOf(FightPos.class, context.command("fightpos"));
        assertInstanceOf(MapStats.class, context.command("mapstats"));
        assertInstanceOf(Movement.class, context.command("movement"));

        assertContainsType(GenItem.class, context.commands());
        assertContainsType(FightPos.class, context.commands());
        assertContainsType(MapStats.class, context.commands());
        assertContainsType(Movement.class, context.commands());
    }
}
