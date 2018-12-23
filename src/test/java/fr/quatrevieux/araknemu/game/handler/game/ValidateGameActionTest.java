package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionFactory;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.game.action.GameActionRequest;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidateGameActionTest extends GameBaseCase {
    private ValidateGameAction handler;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        handler = new ValidateGameAction(
            container.get(ActionFactory.class)
        );

        map = explorationPlayer().map();
    }

    @Test
    void handleSuccessMove() throws Exception {
        handler.handle(
            session,
            new GameActionRequest(
                ActionType.MOVE.id(),
                new String[] {"bftdgl"}
            )
        );

        requestStack.assertLast(
            new GameActionResponse("1", ActionType.MOVE, explorationPlayer().id(), "aexbftdgl")
        );

        assertTrue(explorationPlayer().interactions().busy());
    }

    @Test
    void handleBadRequest() {
        assertThrows(ErrorPacket.class, () -> handler.handle(session, new GameActionRequest(ActionType.NONE.id(), new String[0])));
    }

    @Test
    void handleFailedWhenInteracting() throws SQLException, ContainerException {
        Interaction interaction = Mockito.mock(Interaction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);
        explorationPlayer().interactions().start(interaction);

        assertThrows(
            ErrorPacket.class,
            () -> handler.handle(
                session,
                new GameActionRequest(
                    ActionType.MOVE.id(),
                    new String[] {"bftdgl"}
                )
            )
        );
    }
}
