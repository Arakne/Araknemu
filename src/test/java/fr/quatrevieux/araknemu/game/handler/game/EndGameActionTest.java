package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.action.Move;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionAcknowledge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class EndGameActionTest extends GameBaseCase {
    private EndGameAction handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new EndGameAction();
    }

    @Test
    void handleSuccess() throws Exception {
        dataSet.pushMaps();

        ExplorationPlayer player = explorationPlayer();

        player.actionQueue().push(
            new Move(
                player,
                player.map().decoder().decodePath("bftdgl", 279)
            )
        );

        handler.handle(session, new GameActionAcknowledge(1));

        assertFalse(player.actionQueue().isBusy());
        assertEquals(395, player.position().cell());
    }
}