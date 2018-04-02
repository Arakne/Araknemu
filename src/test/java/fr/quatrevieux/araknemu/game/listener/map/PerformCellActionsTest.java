package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.CellAction;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.CellActionPerformer;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.MapTriggers;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.Teleport;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class PerformCellActionsTest extends GameBaseCase {
    private PerformCellActions listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        Map<CellAction, CellActionPerformer> actions = new HashMap<>();
        actions.put(CellAction.TELEPORT, new Teleport(
            container.get(ExplorationMapService.class)
        ));

        dataSet.pushMaps();
        listener = new PerformCellActions(
            new MapTriggers(
                Collections.singleton(new MapTrigger(1, 10300, 120, CellAction.TELEPORT, "10540,156", "")),
                actions
            )
        );
    }

    @Test
    void onEmptyCellDoNothing() throws SQLException, ContainerException {
        explorationPlayer().move(147);
        requestStack.clear();

        listener.on(
            new PlayerMoveFinished(explorationPlayer())
        );

        requestStack.assertEmpty();
        assertFalse(explorationPlayer().interactions().busy());
    }

    @Test
    void onNonEmptyCell() throws SQLException, ContainerException {
        explorationPlayer().move(120);

        listener.on(
            new PlayerMoveFinished(explorationPlayer())
        );

        assertTrue(explorationPlayer().interactions().busy());
        requestStack.assertLast(
            new GameActionResponse(1, ActionType.CHANGE_MAP, explorationPlayer().id(), "")
        );
    }
}
