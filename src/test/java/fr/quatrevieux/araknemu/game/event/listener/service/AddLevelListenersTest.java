package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.game.event.listener.player.SendLevelUp;
import fr.quatrevieux.araknemu.game.event.listener.player.SendPlayerXp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddLevelListenersTest extends GameBaseCase {
    private AddLevelListeners listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new AddLevelListeners();
    }

    @Test
    void onPlayerLoaded() throws SQLException, ContainerException {
        listener.on(new PlayerLoaded(gamePlayer()));

        assertTrue(gamePlayer().dispatcher().has(SendPlayerXp.class));
        assertTrue(gamePlayer().dispatcher().has(SendLevelUp.class));
    }
}