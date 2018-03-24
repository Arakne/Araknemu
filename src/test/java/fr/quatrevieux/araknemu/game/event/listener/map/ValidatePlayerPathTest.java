package fr.quatrevieux.araknemu.game.event.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.exploration.action.PlayerMoving;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Move;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.PathException;
import fr.quatrevieux.araknemu.game.world.map.PathStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ValidatePlayerPathTest extends GameBaseCase {
    private ValidatePlayerPath listener;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        listener = new ValidatePlayerPath(
            map = explorationPlayer().map()
        );
    }

    @Test
    void onPlayerMovingWithNonWalkableCell() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        PlayerMoving event = new PlayerMoving(
            player,
            new Move(
                player,
                Arrays.asList(
                    new PathStep(279, Direction.WEST),
                    new PathStep(278, Direction.WEST)
                )
            )
        );

        listener.on(event);

        assertEquals(1, event.action().path().size());
    }

    @Test
    void onPlayerMovingWithValidPath() throws SQLException, ContainerException, PathException {
        ExplorationPlayer player = explorationPlayer();

        PlayerMoving event = new PlayerMoving(
            player,
            new Move(
                player,
                map.decoder().decodePath("bftdgl", 279)
            )
        );

        listener.on(event);

        assertEquals("aexbftdgl", map.decoder().encodePath(event.action().path()));
        assertEquals(395, event.action().path().get(event.action().path().size() - 1).cell());
    }
}
