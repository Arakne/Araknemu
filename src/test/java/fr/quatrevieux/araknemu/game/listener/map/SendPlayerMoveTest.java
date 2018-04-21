package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoving;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Move;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathStep;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

class SendPlayerMoveTest extends GameBaseCase {
    private SendPlayerMove listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendPlayerMove(
            explorationPlayer().map()
        );
    }

    @Test
    void onPlayerMoving() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();
        Move move = new Move(
            player,
            new Path<>(
                new Decoder<>(player.map()),
                Arrays.asList(
                    new PathStep(explorationPlayer().map().get(100), Direction.WEST),
                    new PathStep(explorationPlayer().map().get(99), Direction.WEST),
                    new PathStep(explorationPlayer().map().get(98), Direction.WEST)
                )
            )
        );
        move.setId(123);

        listener.on(
            new PlayerMoving(player, move)
        );

        requestStack.assertLast(
            new GameActionResponse(
                123,
                ActionType.MOVE,
                player.id(),
                "ebI"
            )
        );
    }
}
