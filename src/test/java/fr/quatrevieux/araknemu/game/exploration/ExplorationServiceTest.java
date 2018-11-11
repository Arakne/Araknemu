package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.listener.player.InitializeGame;
import fr.quatrevieux.araknemu.game.listener.player.StopExploration;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ExplorationServiceTest extends GameBaseCase {
    private ExplorationService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new ExplorationService(
            container.get(ExplorationMapService.class),
            container.get(Dispatcher.class)
        );

        dataSet.pushMaps();
    }

    @Test
    void create() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        ExplorationPlayer explorationPlayer = service.create(player);

        assertTrue(explorationPlayer.dispatcher().has(InitializeGame.class));
        assertTrue(explorationPlayer.dispatcher().has(StopExploration.class));
    }
}
