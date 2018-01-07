package fr.quatrevieux.araknemu.game.event.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.exploration.action.PlayerMoving;
import fr.quatrevieux.araknemu.game.exploration.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.action.Move;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.PathStep;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SendPlayerMoveTest extends GameBaseCase {
    private SendPlayerMove listener;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        listener = new SendPlayerMove(
            map = container.get(ExplorationMapService.class).load(10300)
        );
    }

    @Test
    void onPlayerMoving() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();
        player.join(map);

        listener.on(
            new PlayerMoving(
                player,
                new Move(
                    1,
                    player,
                    Arrays.asList(
                        new PathStep(100, Direction.WEST),
                        new PathStep(99, Direction.WEST),
                        new PathStep(98, Direction.WEST)
                    )
                )
            )
        );

        requestStack.assertLast(
            new GameActionResponse(
                1,
                ActionType.MOVE,
                player.id(),
                "ebI"
            )
        );
    }
}
