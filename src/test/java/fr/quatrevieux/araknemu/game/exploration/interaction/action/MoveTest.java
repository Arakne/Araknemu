package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.world.map.path.Decoder;
import fr.quatrevieux.araknemu.game.world.map.path.Path;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest extends GameBaseCase {
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        player = explorationPlayer();
    }

    @Test
    void success() throws Exception {
        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("bftdgl", player.map().get(279))
        );

        move.setId(1);

        player.interactions().push(move);

        requestStack.assertLast(
            new GameActionResponse(
                1,
                ActionType.MOVE,
                gamePlayer().id(),
                "aexbftdgl"
            )
        );

        assertTrue(player.interactions().busy());

        player.interactions().end(1);
        assertFalse(player.interactions().busy());

        assertEquals(395, player.position().cell());
    }

    @Test
    void data() throws Exception {
        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("bftdgl", player.map().get(279))
        );

        move.setId(1);

        player.interactions().push(move);

        assertEquals(1, move.id());
        assertEquals(player, move.performer());
        assertArrayEquals(new Object[] {"aexbftdgl"}, move.arguments());
        assertEquals(ActionType.MOVE, move.type());
    }

    @Test
    void invalidPath() {
        Move move = new Move(
            player,
            new Path<>(new Decoder<>(player.map()))
        );

        move.setId(1);

        assertThrows(Exception.class, () -> player.interactions().push(move), "Empty path");
        assertFalse(player.interactions().busy());
    }

    @Test
    void moveWithCancel() throws Exception {
        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("bftdgl", player.map().get(279))
        );

        move.setId(1);

        player.interactions().push(move);

        requestStack.assertLast(
            new GameActionResponse(
                1,
                ActionType.MOVE,
                gamePlayer().id(),
                "aexbftdgl"
            )
        );

        assertTrue(player.interactions().busy());

        player.interactions().cancel(1, "294");
        assertFalse(player.interactions().busy());

        assertEquals(294, player.position().cell());
    }

    @Test
    void moveWithCancelNull() throws Exception {
        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("bftdgl", player.map().get(279))
        );

        move.setId(1);

        player.interactions().push(move);

        requestStack.assertLast(
            new GameActionResponse(
                1,
                ActionType.MOVE,
                gamePlayer().id(),
                "aexbftdgl"
            )
        );

        assertTrue(player.interactions().busy());

        player.interactions().cancel(1, null);
        assertFalse(player.interactions().busy());

        assertEquals(279, player.position().cell());
    }

    @Test
    void moveWithCancelNotInPath() throws Exception {
        Move move = new Move(
            player,
            new Decoder<>(player.map()).decode("bftdgl", player.map().get(279))
        );

        move.setId(1);

        player.interactions().push(move);

        requestStack.assertLast(
            new GameActionResponse(
                1,
                ActionType.MOVE,
                gamePlayer().id(),
                "aexbftdgl"
            )
        );

        assertTrue(player.interactions().busy());

        assertThrows(Exception.class, () -> player.interactions().cancel(1, "410"), "Invalid cell");
        assertFalse(player.interactions().busy());

        assertEquals(279, player.position().cell());
    }
}
