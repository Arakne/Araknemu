package fr.quatrevieux.araknemu.game.exploration.action.factory;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.action.Action;
import fr.quatrevieux.araknemu.game.exploration.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.action.Move;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExplorationActionFactoryTest extends GameBaseCase {
    private ExplorationActionFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ExplorationActionFactory();

        dataSet.pushMaps();
    }

    @Test
    void createNotFound() {
        assertThrows(
            Exception.class,
            () -> factory.create(gamePlayer(), new GameActionRequest(ActionType.NONE, new String[] {})),
            "No factory found for game action : NONE"
        );
    }

    @Test
    void createMove() throws Exception {
        gamePlayer().goTo(gamePlayer().position().newCell(100));
        gamePlayer().join(
            container.get(ExplorationMapService.class).load(10300)
        );

        Action action = factory.create(gamePlayer(), new GameActionRequest(ActionType.MOVE, new String[] {"ebIgbf"}));

        assertTrue(action instanceof Move);
        Move move = (Move) action;

        assertEquals(1, move.id());
        assertEquals(4, move.path().size());
        assertEquals(100, move.path().get(0).cell());
        assertEquals(99, move.path().get(1).cell());
        assertEquals(98, move.path().get(2).cell());
        assertEquals(69, move.path().get(3).cell());
    }
}
