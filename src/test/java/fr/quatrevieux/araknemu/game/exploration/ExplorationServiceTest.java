package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.listener.player.InitializeGame;
import fr.quatrevieux.araknemu.game.event.listener.player.SendMapData;
import fr.quatrevieux.araknemu.game.event.listener.player.StopExploration;
import fr.quatrevieux.araknemu.game.event.listener.player.exploration.LeaveExplorationForFight;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.factory.ExplorationActionFactory;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.game.CreateGameRequest;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.game.GameCreated;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
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
    void create() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        ExplorationPlayer explorationPlayer = service.create(player);

        assertTrue(explorationPlayer.dispatcher().has(InitializeGame.class));
        assertTrue(explorationPlayer.dispatcher().has(SendMapData.class));
        assertTrue(explorationPlayer.dispatcher().has(StopExploration.class));
        assertTrue(explorationPlayer.dispatcher().has(LeaveExplorationForFight.class));
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

        assertTrue(player.interactions().busy());
    }
}
