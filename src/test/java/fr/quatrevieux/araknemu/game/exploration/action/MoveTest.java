package fr.quatrevieux.araknemu.game.exploration.action;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
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
            1,
            player,
            player.map().decoder().decodePath("bftdgl", 279)
        );

        player.actionQueue().push(move);

        requestStack.assertLast(
            new GameActionResponse(
                1,
                ActionType.MOVE,
                gamePlayer().id(),
                "aexbftdgl"
            )
        );

        assertTrue(player.actionQueue().isBusy());

        player.actionQueue().end(1);
        assertFalse(player.actionQueue().isBusy());

        assertEquals(395, player.position().cell());
    }

    @Test
    void invalidPath() {
        Move move = new Move(
            1,
            player,
            Collections.EMPTY_LIST
        );

        assertThrows(Exception.class, () -> player.actionQueue().push(move), "Empty path");
        assertFalse(player.actionQueue().isBusy());
    }

    @Test
    void moveWithCancel() throws Exception {
        Move move = new Move(
            1,
            player,
            player.map().decoder().decodePath("bftdgl", 279)
        );

        player.actionQueue().push(move);

        requestStack.assertLast(
            new GameActionResponse(
                1,
                ActionType.MOVE,
                gamePlayer().id(),
                "aexbftdgl"
            )
        );

        assertTrue(player.actionQueue().isBusy());

        player.actionQueue().cancel(1, "294");
        assertFalse(player.actionQueue().isBusy());

        assertEquals(294, player.position().cell());
    }

    @Test
    void moveWithCancelNotInPath() throws Exception {
        Move move = new Move(
            1,
            player,
            player.map().decoder().decodePath("bftdgl", 279)
        );

        player.actionQueue().push(move);

        requestStack.assertLast(
            new GameActionResponse(
                1,
                ActionType.MOVE,
                gamePlayer().id(),
                "aexbftdgl"
            )
        );

        assertTrue(player.actionQueue().isBusy());

        assertThrows(Exception.class, () -> player.actionQueue().cancel(1, "410"), "Invalid cell");
        assertFalse(player.actionQueue().isBusy());

        assertEquals(279, player.position().cell());
    }
}
