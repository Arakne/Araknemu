package fr.quatrevieux.araknemu.game.listener.service;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.listener.player.SendMapData;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.listener.player.SendMapData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddExplorationMapListenersTest extends GameBaseCase {
    private AddExplorationMapListeners listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new AddExplorationMapListeners();
    }

    @Test
    void onExplorationPlayerCreated() throws SQLException, ContainerException {
        ExplorationPlayer player = new ExplorationPlayer(gamePlayer(true));

        listener.on(new ExplorationPlayerCreated(player));

        assertTrue(player.dispatcher().has(SendMapData.class));
    }
}
