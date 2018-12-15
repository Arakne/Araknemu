package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.listener.map.SendSpriteRestrictions;
import fr.quatrevieux.araknemu.game.listener.player.InitializeGame;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExplorationServiceTest extends GameBaseCase {
    private ExplorationService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new ExplorationService(
            container.get(ExplorationMapService.class),
            container.get(Dispatcher.class)
        );

        dataSet.pushMaps();
    }

    @Test
    void create() throws SQLException, ContainerException {
        GamePlayer player = gamePlayer();

        ExplorationPlayer explorationPlayer = service.create(player);

        assertTrue(explorationPlayer.dispatcher().has(InitializeGame.class));
    }

    @Test
    void listeners() throws Exception {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        dispatcher.dispatch(new MapLoaded(map));

        assertTrue(map.dispatcher().has(SendSpriteRestrictions.class));
    }
}
