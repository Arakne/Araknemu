package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.common.Disconnected;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SavePlayerTest extends GameBaseCase {
    private SavePlayer listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SavePlayer(gamePlayer());
    }

    @Test
    void onDisconnect() throws SQLException, ContainerException {
        gamePlayer().setPosition(new Position(78, 96));

        listener.on(new Disconnected());

        assertEquals(new Position(78, 96), dataSet.refresh(new Player(gamePlayer().id())).position());
    }
}
