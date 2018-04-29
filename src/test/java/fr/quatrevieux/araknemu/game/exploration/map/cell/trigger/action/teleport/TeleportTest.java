package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TeleportTest extends GameBaseCase {
    private Teleport teleport;
    private ExplorationMapService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
        service = container.get(ExplorationMapService.class);
    }

    @Test
    void performOnSameMap() throws Exception {
        teleport = new Teleport(service, 123, new Position(10300, 321));
        teleport.perform(explorationPlayer());

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
        teleport = new Teleport(service, 123, new Position(10540, 321));
        teleport.perform(explorationPlayer());

        assertTrue(explorationPlayer().interactions().busy());

        requestStack.assertLast(
            new GameActionResponse(1, ActionType.CHANGE_MAP, explorationPlayer().id(), "")
        );
    }
}
