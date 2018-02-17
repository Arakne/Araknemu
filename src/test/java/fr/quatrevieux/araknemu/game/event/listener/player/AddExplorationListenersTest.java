package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.exploration.StartExploration;
import fr.quatrevieux.araknemu.game.event.listener.map.SendAccessories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddExplorationListenersTest extends GameBaseCase {
    private AddExplorationListeners listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new AddExplorationListeners();
    }

    @Test
    void onStartExploration() throws SQLException, ContainerException {
        listener.on(
            new StartExploration(explorationPlayer())
        );

        assertTrue(explorationPlayer().dispatcher().has(SendAccessories.class));
    }
}
