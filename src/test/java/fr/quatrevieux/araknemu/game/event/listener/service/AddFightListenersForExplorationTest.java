package fr.quatrevieux.araknemu.game.event.listener.service;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.exploration.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.event.listener.player.exploration.LeaveExplorationForFight;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class AddFightListenersForExplorationTest extends GameBaseCase {
    private AddFightListenersForExploration listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new AddFightListenersForExploration();
    }

    @Test
    void onExplorationPlayerCreated() throws SQLException, ContainerException {
        ExplorationPlayer player = new ExplorationPlayer(gamePlayer(true));

        listener.on(new ExplorationPlayerCreated(player));

        assertTrue(player.dispatcher().has(LeaveExplorationForFight.class));
    }
}