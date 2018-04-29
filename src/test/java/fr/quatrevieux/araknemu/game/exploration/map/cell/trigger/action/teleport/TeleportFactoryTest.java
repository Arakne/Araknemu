package fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.CellAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TeleportFactoryTest extends GameBaseCase {
    private TeleportFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new TeleportFactory(container.get(ExplorationMapService.class));
    }

    @Test
    void create() {
        CellAction action = factory.create(new MapTrigger(10340, 256, Teleport.ACTION_ID, "1300,123", "-1"));

        assertInstanceOf(Teleport.class, action);
        assertEquals(256, action.cell());
    }
}
