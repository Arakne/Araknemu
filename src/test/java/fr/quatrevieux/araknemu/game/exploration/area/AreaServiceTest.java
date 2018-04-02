package fr.quatrevieux.araknemu.game.exploration.area;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.data.living.repository.environment.SubAreaRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.listener.player.InitializeAreas;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import java.sql.SQLException;

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
    void playerLoadedListener() throws SQLException, ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new PlayerLoaded(gamePlayer()));

        assertTrue(gamePlayer().dispatcher().has(InitializeAreas.class));
    }
}
