package fr.quatrevieux.araknemu.game.event.listener.player.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.BlockingAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;

class LeaveExplorationTest extends GameBaseCase {
    private LeaveExploration runnable;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        runnable = new LeaveExploration(
            explorationPlayer()
        );
    }

    @Test
    void runWillRemoveFromMap() throws SQLException, ContainerException {
        runnable.run();

        assertFalse(explorationPlayer().map().players().contains(explorationPlayer()));
    }

    @Test
    void runWillStopInteractions() throws Exception {
        explorationPlayer().interactions().push(Mockito.mock(BlockingAction.class));

        runnable.run();

        assertFalse(explorationPlayer().interactions().busy());
    }
}
