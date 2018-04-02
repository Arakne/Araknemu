package fr.quatrevieux.araknemu.game.listener.service;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.game.listener.player.fight.AttachFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddFightListenersForPlayerTest extends GameBaseCase {
    private AddFightListenersForPlayer listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new AddFightListenersForPlayer();
    }

    @Test
    void onPlayerLoaded() throws SQLException, ContainerException {
        listener.on(
            new PlayerLoaded(gamePlayer())
        );

        assertTrue(gamePlayer().dispatcher().has(AttachFighter.class));
    }
}
