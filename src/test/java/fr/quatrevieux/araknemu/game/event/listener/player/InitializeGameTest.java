package fr.quatrevieux.araknemu.game.event.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.exploration.StartExploration;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class InitializeGameTest extends GameBaseCase {
    private InitializeGame listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new InitializeGame(gamePlayer());
    }

    @Test
    void onStartExploration() throws SQLException, ContainerException {
        listener.on(new StartExploration());

        requestStack.assertAll(
            new GameCreated(CreateGameRequest.Type.EXPLORATION),
            new Stats(gamePlayer())
        );
    }
}
