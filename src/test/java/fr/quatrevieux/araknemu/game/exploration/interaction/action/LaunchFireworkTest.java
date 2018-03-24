package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LaunchFireworkTest extends GameBaseCase {
    private LaunchFirework action;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        action = new LaunchFirework(
            explorationPlayer(),
            150,
            604,
            12
        );
    }

    @Test
    void id() {
        action.setId(5);
        assertEquals(5, action.id());
    }

    @Test
    void performer() throws SQLException, ContainerException {
        assertSame(explorationPlayer(), action.performer());
    }

    @Test
    void type() {
        assertEquals(ActionType.FIREWORK, action.type());
    }

    @Test
    void arguments() {
        assertArrayEquals(
            new Object[] {"150,604,11,8,12"},
            action.arguments()
        );
    }

    @Test
    void start() {
        action.start();

        requestStack.assertLast(
            new GameActionResponse(action)
        );
    }

    @Test
    void cancel() {
        action.cancel(null);
    }
}
