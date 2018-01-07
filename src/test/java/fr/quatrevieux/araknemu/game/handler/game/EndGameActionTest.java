package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.action.Move;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.map.PathException;
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

        GamePlayer player = gamePlayer();
        player.join(
            container.get(ExplorationMapService.class).load(10300)
        );

        player.actionQueue().push(
            new Move(
                1,
                player,
                player.map().decoder().decodePath("bftdgl", 279)
            )
        );

        handler.handle(session, new GameActionAcknowledge(1));

        assertFalse(player.actionQueue().isBusy());
        assertEquals(395, player.position().cell());
    }
}