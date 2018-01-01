package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreated;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreationError;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateGameTest extends GameBaseCase {
    private CreateGame handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new CreateGame(
            container.get(ExplorationService.class)
        );

        dataSet.pushMaps();
        gamePlayer();
    }

    @Test
    void handleBadGameType() throws Exception {
        try {
            handler.handle(session, new CreateGameRequest(CreateGameRequest.Type.FIGHT));

            fail("ErrorPacket should be thrown");
        } catch (ErrorPacket e) {
            assertEquals(new GameCreationError().toString(), e.packet().toString());
        }
    }

    @Test
    void handleSuccess() throws Exception {
        handler.handle(session, new CreateGameRequest(CreateGameRequest.Type.EXPLORATION));

        requestStack.assertAll(
            new GameCreated(CreateGameRequest.Type.EXPLORATION),
            new Stats(gamePlayer()),
            new MapData(gamePlayer().map())
        );
    }
}
