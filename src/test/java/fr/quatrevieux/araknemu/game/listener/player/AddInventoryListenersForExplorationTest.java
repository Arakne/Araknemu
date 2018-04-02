package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.listener.map.SendAccessories;
import fr.quatrevieux.araknemu.game.listener.service.AddInventoryListenersForExploration;
import fr.quatrevieux.araknemu.game.listener.map.SendAccessories;
import fr.quatrevieux.araknemu.game.listener.service.AddInventoryListenersForExploration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddInventoryListenersForExplorationTest extends GameBaseCase {
    private AddInventoryListenersForExploration listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new AddInventoryListenersForExploration();
    }

    @Test
    void onExplorationPlayerCreated() throws SQLException, ContainerException {
        listener.on(
            new ExplorationPlayerCreated(explorationPlayer())
        );

        assertTrue(explorationPlayer().dispatcher().has(SendAccessories.class));
    }
}
