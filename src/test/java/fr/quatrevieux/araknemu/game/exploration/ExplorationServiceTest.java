package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.listener.player.InitializeGame;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreated;
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

        service = new ExplorationService();
    }

    @Test
    void start() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        service.start(player);

        assertTrue(player.dispatcher().has(InitializeGame.class));

        requestStack.assertAll(
            new GameCreated(CreateGameRequest.Type.EXPLORATION),
            new Stats(gamePlayer())
        );
    }
}