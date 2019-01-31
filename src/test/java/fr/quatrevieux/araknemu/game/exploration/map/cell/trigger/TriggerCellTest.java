package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.Teleport;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class TriggerCellTest extends GameBaseCase {
    private TriggerCell cell;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        cell = new TriggerCell(
            123,
            new Teleport(
                container.get(ExplorationMapService.class),
                123,
                new Position(10340, 321)
            ),
            container.get(ExplorationMapService.class).load(10300)
        );
    }

    @Test
    void values() throws ContainerException {
        assertEquals(123, cell.id());
        assertTrue(cell.walkable());
        assertSame(container.get(ExplorationMapService.class).load(10300), cell.map());
    }

    @Test
    void equals() {
        assertEquals(cell, cell);
        assertNotEquals(cell, new Object());
        assertNotEquals(cell, new TriggerCell(321, null, null));
    }

    @Test
    void hashCodeValue() {
        assertEquals(cell.hashCode(), cell.hashCode());
        assertNotEquals(cell.hashCode(), new TriggerCell(321, null, null).hashCode());
    }

    @Test
    void onStop() throws SQLException, ContainerException {
        cell.onStop(explorationPlayer());

        assertTrue(explorationPlayer().interactions().busy());

        requestStack.assertLast(
            new GameActionResponse("1", ActionType.CHANGE_MAP, explorationPlayer().id(), "")
        );
    }
}
