package fr.quatrevieux.araknemu.game.exploration.map.trigger;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.MapData;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TeleportTest extends GameBaseCase {
    private Teleport teleport;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        teleport = new Teleport(
            container.get(ExplorationMapService.class)
        );
    }

    @Test
    void performOnSameMap() throws Exception {
        teleport.perform(
            new MapTrigger(1, 10300, 456, CellAction.TELEPORT, "10300,321", ""),
            explorationPlayer()
        );

        assertEquals(
            new Position(10300, 321),
            explorationPlayer().position()
        );

        assertFalse(explorationPlayer().interactions().busy());

        requestStack.assertLast(
            new AddSprites(
                Collections.singleton(explorationPlayer().sprite())
            )
        );
    }

    @Test
    void teleportOnOtherMap() throws Exception {
        teleport.perform(
            new MapTrigger(1, 10300, 456, CellAction.TELEPORT, "10540,321", ""),
            explorationPlayer()
        );

        assertTrue(explorationPlayer().interactions().busy());

        requestStack.assertLast(
            new GameActionResponse(1, ActionType.CHANGE_MAP, explorationPlayer().id(), "")
        );
    }
}
