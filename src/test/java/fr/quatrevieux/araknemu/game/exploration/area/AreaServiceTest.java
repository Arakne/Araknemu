package fr.quatrevieux.araknemu.game.exploration.area;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.repository.environment.SubAreaRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.listener.service.RegisterAreaListeners;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import static org.junit.jupiter.api.Assertions.*;

class AreaServiceTest extends GameBaseCase {
    private AreaService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new AreaService(
            container.get(SubAreaRepository.class)
        );

        dataSet.pushSubAreas();
        service.preload(NOPLogger.NOP_LOGGER);
    }

    @Test
    void list() {
        assertCount(4, service.list());
        assertSame(
            service.list(),
            service.list()
        );
    }

    @Test
    void dispatcher() throws ContainerException {
        assertTrue(container.get(ListenerAggregate.class).has(RegisterAreaListeners.class));
    }
}
