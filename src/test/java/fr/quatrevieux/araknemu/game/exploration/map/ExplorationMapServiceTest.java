package fr.quatrevieux.araknemu.game.exploration.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.event.ExplorationPlayerCreated;
import fr.quatrevieux.araknemu.game.exploration.map.cell.CellLoader;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.TriggerCell;
import fr.quatrevieux.araknemu.game.exploration.map.cell.trigger.action.teleport.Teleport;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.listener.map.*;
import fr.quatrevieux.araknemu.game.listener.player.SendMapData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ExplorationMapServiceTest extends GameBaseCase {
    private ExplorationMapService service;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new ExplorationMapService(
            container.get(MapTemplateRepository.class),
            dispatcher = new DefaultListenerAggregate(),
            container.get(CellLoader.class)
        );

        dataSet.pushMaps();
    }

    @Test
    void load() throws SQLException, ContainerException {
        AtomicReference<MapLoaded> ref = new AtomicReference<>();
        dispatcher.add(MapLoaded.class, ref::set);

        dataSet.pushTrigger(new MapTrigger(10540, 120, Teleport.ACTION_ID, "123,45", ""));

        ExplorationMap map = service.load(10540);

        assertEquals(10540, map.id());
        assertEquals(0, map.sprites().size());
        assertSame(map, ref.get().map());

        ref.set(null);

        assertSame(map, service.load(10540));
        assertNull(ref.get());
        assertNotSame(map, service.load(10300));

        assertTrue(map.dispatcher().has(SendNewSprite.class));
        assertTrue(map.dispatcher().has(ValidatePlayerPath.class));
        assertTrue(map.dispatcher().has(SendPlayerMove.class));
        assertTrue(map.dispatcher().has(SendSpriteRemoved.class));
        assertTrue(map.dispatcher().has(SendPlayerChangeCell.class));
    }

    @Test
    void listeners() throws SQLException, ContainerException {
        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        ExplorationPlayer player = new ExplorationPlayer(gamePlayer(true));
        dispatcher.dispatch(new ExplorationPlayerCreated(player));

        assertTrue(player.dispatcher().has(SendMapData.class));
    }

    @Test
    void preloadWithTriggers() throws SQLException, ContainerException {
        dataSet.pushTrigger(new MapTrigger(10300, 120, Teleport.ACTION_ID, "123,45", ""));
        dataSet.pushTrigger(new MapTrigger(10300, 125, Teleport.ACTION_ID, "123,45", ""));
        dataSet.pushTrigger(new MapTrigger(10300, 121, Teleport.ACTION_ID, "123,45", ""));

        assertInstanceOf(TriggerCell.class, service.load(10300).get(120));
        assertInstanceOf(TriggerCell.class, service.load(10300).get(121));
        assertInstanceOf(TriggerCell.class, service.load(10300).get(125));

        Logger logger = Mockito.mock(Logger.class);
        service.preload(logger);

        Mockito.verify(logger).info("Loading maps...");
        Mockito.verify(logger).info(Mockito.eq("{} maps successfully loaded in {}ms"), Mockito.eq(3), Mockito.any());
    }
}
