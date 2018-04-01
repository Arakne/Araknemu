package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.listener.service.AddExplorationMapListeners;
import fr.quatrevieux.araknemu.game.exploration.map.trigger.CellAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ExplorationMapServiceTest extends GameBaseCase {
    private ExplorationMapService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new ExplorationMapService(
            container.get(MapTemplateRepository.class),
            container.get(MapTriggerRepository.class),
            container.get(ListenerAggregate.class)
        );

        dataSet.pushMaps();
    }

    @Test
    void load() throws SQLException, ContainerException {
        dataSet.pushTrigger(new MapTrigger(1, 10540, 120, CellAction.TELEPORT, "123,45", ""));

        ExplorationMap map = service.load(10540);

        assertEquals(10540, map.id());
        assertEquals(0, map.sprites().size());

        assertSame(map, service.load(10540));
        assertNotSame(map, service.load(10300));
    }

    @Test
    void preload() throws ContainerException {
        service.preload(NOPLogger.NOP_LOGGER);

        assertTrue(container.get(ListenerAggregate.class).has(AddExplorationMapListeners.class));
    }

    @Test
    void preloadWithTriggers() throws SQLException, ContainerException {
        dataSet.pushTrigger(new MapTrigger(1, 10300, 120, CellAction.TELEPORT, "123,45", ""));
        dataSet.pushTrigger(new MapTrigger(2, 10300, 120, CellAction.TELEPORT, "123,45", ""));
        dataSet.pushTrigger(new MapTrigger(3, 10300, 121, CellAction.TELEPORT, "123,45", ""));

        service.preload(NOPLogger.NOP_LOGGER);
    }
}
