package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.action.Move;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.map.PathException;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionCancel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

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

        player.actionQueue().push(move);

        handler.handle(session, new GameActionCancel(1, "294"));

        assertFalse(player.actionQueue().isBusy());
        assertEquals(294, player.position().cell());
    }
}