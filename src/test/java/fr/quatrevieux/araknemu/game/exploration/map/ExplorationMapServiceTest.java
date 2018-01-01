package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import static org.junit.jupiter.api.Assertions.*;

class ExplorationMapServiceTest extends GameBaseCase {
    private ExplorationMapService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new ExplorationMapService(
            container.get(MapTemplateRepository.class)
        );

        dataSet.pushMaps();
    }

    @Test
    void load() {
        ExplorationMap map = service.load(10540);

        assertEquals(10540, map.id());
        assertEquals(0, map.sprites().size());

        assertSame(map, service.load(10540));
        assertNotSame(map, service.load(10300));
    }

    @Test
    void preload() {
        service.preload(NOPLogger.NOP_LOGGER);
    }
}
