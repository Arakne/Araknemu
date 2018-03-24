package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Move;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionCancel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CancelGameActionTest extends GameBaseCase {
    private CancelGameAction handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new CancelGameAction();
    }

    @Test
    void handleCancelMove() throws Exception {
        dataSet.pushMaps();

        ExplorationPlayer player = explorationPlayer();

        Move move = new Move(
            player,
            player.map().decoder().decodePath("bftdgl", 279)
        );

        player.interactions().push(move);

        handler.handle(session, new GameActionCancel(1, "294"));

        assertFalse(player.interactions().busy());
        assertEquals(294, player.position().cell());
    }
}