package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.Teleport;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
class PerformCellActionsTest extends GameBaseCase {
    private PerformCellActions listener;
    private ExplorationMap map;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        dataSet.pushTrigger(new MapTrigger(10300, 120, Teleport.ACTION_ID, "10540,156", ""));
        listener = new PerformCellActions();
        map = container.get(ExplorationMapService.class).load(10300);
        player = explorationPlayer();
        requestStack.clear();
    }

    @Test
    void onBasicCellDoNothing() {
        listener.on(new PlayerMoveFinished(player, map.get(456)));

        requestStack.assertEmpty();
        assertFalse(player.interactions().busy());
    }

    @Test
    void onNonEmptyCell() throws SQLException, ContainerException {
        listener.on(new PlayerMoveFinished(player, map.get(120)));

        assertTrue(player.interactions().busy());
        requestStack.assertLast(
            new GameActionResponse(1, ActionType.CHANGE_MAP, explorationPlayer().id(), "")
        );
    }
}
