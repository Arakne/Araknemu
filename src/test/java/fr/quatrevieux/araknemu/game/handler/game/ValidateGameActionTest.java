package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationService;
import fr.quatrevieux.araknemu.game.exploration.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidateGameActionTest extends GameBaseCase {
    private ValidateGameAction handler;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        handler = new ValidateGameAction(
            container.get(ExplorationService.class)
        );

        map = container.get(ExplorationMapService.class).load(10300);
        gamePlayer().join(map);
        gamePlayer().goTo(new Position(10300, 279));
    }

    @Test
    void handleSuccessMove() throws Exception {
        handler.handle(
            session,
            new GameActionRequest(
                ActionType.MOVE,
                new String[] {"bftdgl"}
            )
        );

        requestStack.assertLast(
            new GameActionResponse(
                1,
                ActionType.MOVE,
                gamePlayer().id(),
                "aexbftdgl"
            )
        );

        assertTrue(gamePlayer().actionQueue().isBusy());
    }

    @Test
    void handleBadRequest() {
        assertThrows(ErrorPacket.class, () -> handler.handle(session, new GameActionRequest(ActionType.NONE, new String[0])));
    }
}
