package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.common.Disconnected;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class StopExplorationTest extends GameBaseCase {
    private StopExploration listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new StopExploration(
            explorationPlayer()
        );
    }

    @Test
    void onDisconnect() throws SQLException, ContainerException {
        listener.on(new Disconnected());

        assertFalse(explorationPlayer().map().players().contains(explorationPlayer()));
    }
}
