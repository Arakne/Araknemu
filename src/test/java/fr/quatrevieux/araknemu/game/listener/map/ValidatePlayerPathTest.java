package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoving;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Move;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.game.world.map.path.PathException;
import fr.quatrevieux.araknemu.game.world.map.path.PathStep;
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
        map = explorationPlayer().map();

        listener = new ValidatePlayerPath();
    }

    @Test
    void onPlayerMovingWithNonWalkableCell() throws SQLException, ContainerException {
        ExplorationPlayer player = explorationPlayer();

        PlayerMoving event = new PlayerMoving(
            player,
            new Move(
                player,
                new Path<>(
                    new Decoder<>(player.map()),
                    Arrays.asList(
                        new PathStep(map.get(279), Direction.WEST),
                        new PathStep(map.get(278), Direction.WEST)
                    )
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
                new Decoder<>(map).decode("bftdgl", map.get(279))
            )
        );

        listener.on(event);

        assertEquals("aexbftdgl", new Decoder<>(map).encode(event.action().path()));
        assertEquals(map.get(395), event.action().path().get(event.action().path().size() - 1).cell());
    }
}
