package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.listener.player.InitializeGame;
import fr.quatrevieux.araknemu.game.event.listener.player.SendMapData;
import fr.quatrevieux.araknemu.game.event.listener.map.SendNewSprite;
import fr.quatrevieux.araknemu.game.exploration.action.Action;
import fr.quatrevieux.araknemu.game.exploration.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.action.factory.ExplorationActionFactory;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreated;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;
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
            container.get(ExplorationActionFactory.class)
        );

        dataSet.pushMaps();
    }

    @Test
    void start() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        ExplorationPlayer explorationPlayer = service.start(player);

        assertTrue(explorationPlayer.dispatcher().has(InitializeGame.class));
        assertTrue(explorationPlayer.dispatcher().has(SendMapData.class));

        assertNotNull(explorationPlayer.map());
        assertEquals(10540, explorationPlayer.map().id());

        requestStack.assertAll(
            new GameCreated(CreateGameRequest.Type.EXPLORATION),
            new Stats(gamePlayer()),
            new MapData(explorationPlayer.map())
        );
    }

    @Test
    void action() throws Exception {
        ExplorationPlayer player = explorationPlayer();
        gamePlayer().setPosition(new Position(10300, 100));

        service.action(
            player,
            new GameActionRequest(
                ActionType.MOVE,
                new String[] {"ebI"}
            )
        );

        assertTrue(player.actionQueue().isBusy());
    }
}
